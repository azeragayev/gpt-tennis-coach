package com.aaa.gptenniscoach.feature.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaa.gptenniscoach.data.api.dto.AnalyzeResponseDto
import com.aaa.gptenniscoach.data.repo.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlinx.serialization.json.JsonObject

/* ---------- UI MODELS ---------- */

data class QualityUiModel(
    val level: String,
    val score: Double,
    val framesUsed: Double,
    val warnings: List<String>,
    val details: JsonObject? // ✅ added
)

data class ResultsUiState(
    val sessionId: String,

    // --- video source (required by VideoPlayerWithOverlay)
    val videoStorageMode: String? = null,
    val videoLocalPath: String? = null,
    val videoContentUri: String? = null,

    val response: AnalyzeResponseDto? = null,

    val feedbackText: String = "",

    // ✅ structured quality (NOT text)
    val quality: QualityUiModel? = null,

    val overlaySummary: String = "",
    val rawJson: String = ""
)

/* ---------- VIEWMODEL ---------- */

@HiltViewModel
class ResultsViewModel @Inject constructor(
    repo: SessionRepository,
    json: Json,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle["sessionId"])

    val state: StateFlow<ResultsUiState> =
        repo.observeSession(sessionId)
            .map { detail ->
                if (detail == null) {
                    ResultsUiState(
                        sessionId = sessionId,
                        feedbackText = "Session not found."
                    )
                } else {

                    val parsed = runCatching {
                        json.decodeFromString(
                            AnalyzeResponseDto.serializer(),
                            detail.responseJson
                        )
                    }.getOrNull()

                    val feedbackText = buildString {
                        parsed?.feedback?.headline?.let {
                            appendLine(it)
                            appendLine()
                        }
                        parsed?.feedback?.focusPoints?.forEach { fp ->
                            appendLine("• ${fp.title}")
                            appendLine("  Issue: ${fp.currentIssue}")
                            appendLine("  Target: ${fp.target}")
                            appendLine("  Cue: ${fp.feelCue}")
                            appendLine()
                        }
                        parsed?.feedback?.drills?.forEach { dr ->
                            appendLine("• ${dr.title}")
                            appendLine("  Description: ${dr.description}")
                            appendLine()
                        }
                    }.ifBlank { "No feedback returned." }

                    val qualityUi = parsed?.quality?.let {
                        QualityUiModel(
                            level = it.level,
                            score = it.score,
                            framesUsed = it.framesUsed,
                            warnings = it.warnings,
                            details = it.details
                        )
                    }


                    val overlay = parsed?.overlay
                    val overlaySummary =
                        if (overlay == null) {
                            "Overlay: not present"
                        } else {
                            """
                            Overlay v=${overlay.version}
                            FPS=${overlay.fps}
                            Frames=${overlay.frames.size}
                            Window=${overlay.windowStartMs}..${overlay.windowEndMs} ms
                            Landmarks/frame=${overlay.landmarkIds.size}
                            """.trimIndent()
                        }

                    ResultsUiState(
                        sessionId = detail.id,
                        // video
                        videoStorageMode = detail.videoStorageMode,
                        videoLocalPath = detail.videoLocalPath,
                        videoContentUri = detail.videoContentUri,
                        response = parsed,
                        feedbackText = feedbackText,
                        quality = qualityUi,
                        overlaySummary = overlaySummary,
                        rawJson = detail.responseJson
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResultsUiState(sessionId = sessionId)
            )
}
