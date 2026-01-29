package com.aaa.gptenniscoach.data.repo

import android.content.Context
import android.net.Uri
//import android.util.Log
import com.aaa.gptenniscoach.core.Time
import com.aaa.gptenniscoach.core.VideoFiles
import com.aaa.gptenniscoach.data.api.TennisCoachApi
import com.aaa.gptenniscoach.data.api.dto.AnalyzeResponseDto
import com.aaa.gptenniscoach.data.db.dao.SessionDao
import com.aaa.gptenniscoach.data.db.entity.SessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import okio.buffer

/**
 * Implementation of SessionRepository for managing analysis sessions with backend API integration.
 */
class SessionRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dao: SessionDao,
    private val api: TennisCoachApi,
    private val json: Json
) : SessionRepository {

    /**
     * Analyzes a video from content URI, handles upload, and stores results.
     */
    override fun analyzeFromContentUri(
        uri: Uri,
        playerMeta: PlayerMeta
    ): Flow<AnalyzeProgress> = channelFlow {

        try {
            send(AnalyzeProgress.Copying)

            val copied: File = withContext(Dispatchers.IO) {
                VideoFiles.copyToAppStorage(context, uri)
            }

            val playerMetaJson = json.encodeToString(
                kotlinx.serialization.builtins.MapSerializer(
                    kotlinx.serialization.serializer<String>(),
                    kotlinx.serialization.serializer<String>()
                ),
                mapOf(
                    "level" to playerMeta.level,
                    "handedness" to playerMeta.handedness,
                    "goal" to playerMeta.goal,
                    "language" to playerMeta.language,
                    "detailMode" to playerMeta.detailMode
                )
            )

            send(AnalyzeProgress.Uploading(0f))

            val videoPart = MultipartBody.Part.createFormData(
                name = "video",
                filename = copied.name,
                body = ProgressRequestBody(
                    delegate = copied.asRequestBody("video/mp4".toMediaType()),
                    onProgress = { written, total ->
                        val fraction =
                            if (total > 0) written.toFloat() / total else 0f
                        trySend(
                            AnalyzeProgress.Uploading(
                                min(0.99f, max(0f, fraction))
                            )
                        )
                    }
                )
            )

            val playerMetaBody =
                playerMetaJson.toRequestBody("application/json".toMediaType())
            // val includeOverlayBody =
            //    "true".toRequestBody("text/plain".toMediaType())

            send(AnalyzeProgress.Analyzing)

            /* ---------- RAW BACKEND RESPONSE ---------- */

            val rawResponseBody = api.analyzeVideo(
                video = videoPart,
                meta = playerMetaBody,
                includeOverlay = 1
            )

            val rawJson = rawResponseBody.string()

            /* ---------- SAFE DTO PARSE (OPTIONAL) ---------- */

            val parsed = runCatching {
                json.decodeFromString(
                    AnalyzeResponseDto.serializer(),
                    rawJson
                )
            }.getOrNull()

            val entity = SessionEntity(
                id = parsed?.sessionId ?: Time.nowMs().toString(),
                createdAtMs = Time.nowMs(),
                videoLocalPath = copied.absolutePath,
                videoContentUri = uri.toString(),
                videoStorageMode = "APP_COPY",
                playerMetaJson = playerMetaJson,
                requestJson = playerMetaJson,
                responseJson = rawJson, // ✅ FULL JSON STORED
                qualitySummary = parsed?.quality?.level,
                warningsCount = parsed?.quality?.warnings?.size ?: 0
            )

            dao.upsert(entity)

            send(
                AnalyzeProgress.Success(
                    parsed?.sessionId ?: entity.id
                )
            )

        } catch (e: Exception) {
            val (msg, retryable) = mapError(e)
            send(AnalyzeProgress.Error(msg, retryable))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Observes all sessions as summaries ordered by creation date.
     */
    override fun observeSessions(): Flow<List<SessionSummary>> =
        dao.observeAll().map { list ->
            list.map {
                SessionSummary(
                    id = it.id,
                    createdAtMs = it.createdAtMs,
                    quality = it.qualitySummary,
                    warningsCount = it.warningsCount
                )
            }
        }

    /**
     * Observes detailed information for a specific session by ID.
     */
    override fun observeSession(id: String): Flow<SessionDetail?> =
        dao.observeById(id).map { e ->
            e?.let {
                SessionDetail(
                    id = it.id,
                    createdAtMs = it.createdAtMs,
                    videoLocalPath = it.videoLocalPath,
                    videoContentUri = it.videoContentUri,
                    videoStorageMode = it.videoStorageMode,
                    playerMetaJson = it.playerMetaJson,
                    responseJson = it.responseJson // ✅ untouched JSON
                )
            }
        }

    /**
     * Deletes a session and its associated video file from storage.
     */
    override suspend fun deleteSession(id: String) {
        val entity = dao.getByIdOnce(id)
        if (entity != null) {
            dao.deleteById(id)
            if (entity.videoStorageMode == "APP_COPY") {
                entity.videoLocalPath?.let {
                    withContext(Dispatchers.IO) {
                        runCatching { File(it).delete() }
                    }
                }
            }
        }
    }

    /**
     * Deletes all sessions and their associated video files from storage.
     */
    override suspend fun deleteAllSessions() {
        // 1) Fetch all sessions once
        val sessions = dao.getAllOnce()

        // 2) Delete DB rows
        dao.deleteAll()

        // 3) Delete video files (best-effort)
        withContext(Dispatchers.IO) {
            sessions.forEach { entity ->
                if (entity.videoStorageMode == "APP_COPY") {
                    entity.videoLocalPath?.let { path ->
                        runCatching { File(path).delete() }
                    }
                }
            }
        }
    }


    private fun mapError(e: Exception): Pair<String, Boolean> =
        when (e) {
            is SocketTimeoutException ->
                "Request timed out." to true
            is IOException ->
                "Network error." to true
            is HttpException ->
                "Server error (${e.code()})." to (e.code() >= 500)
            else ->
                "Unexpected error: ${e.message}" to true
        }
}

/* ---------- Upload progress helper ---------- */

private class ProgressRequestBody(
    private val delegate: RequestBody,
    private val onProgress: (Long, Long) -> Unit
) : RequestBody() {

    override fun contentType() = delegate.contentType()
    override fun contentLength() = delegate.contentLength()

    override fun writeTo(sink: okio.BufferedSink) {
        val total = contentLength()
        val countingSink = object : okio.ForwardingSink(sink) {
            var written = 0L
            override fun write(source: okio.Buffer, byteCount: Long) {
                super.write(source, byteCount)
                written += byteCount
                onProgress(written, total)
            }
        }
        val buffered = countingSink.buffer()
        delegate.writeTo(buffered)
        buffered.flush()
    }
}
