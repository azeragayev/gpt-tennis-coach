package com.aaa.gptenniscoach.nav

import android.net.Uri
import com.aaa.gptenniscoach.data.repo.PlayerMeta

object Destinations {

    const val CAPTURE = "capture"
    const val ANALYZE = "analyze"
    const val RESULTS = "results"
    const val HISTORY = "history"
    const val DETAIL = "detail"
    const val PREVIEW = "preview"
    const val PREVIEW_RAW = "preview_raw"

    /* ---------- Simple routes ---------- */

    /**
     * Generates route for results screen with session ID.
     */
    fun results(sessionId: String) = "$RESULTS/$sessionId"

    /**
     * Generates route for session detail screen with session ID.
     */
    fun detail(sessionId: String) = "$DETAIL/$sessionId"

    /**
     * Generates route for preview screen with session ID.
     */
    fun preview(sessionId: String) = "$PREVIEW/$sessionId"

    /* ---------- Raw preview with meta ---------- */

    /**
     * Generates route for raw video preview with player metadata query parameters.
     */
    fun previewRaw(
        uri: Uri,
        meta: PlayerMeta
    ): String =
        "$PREVIEW_RAW/${Uri.encode(uri.toString())}" +
                "?level=${Uri.encode(meta.level)}" +
                "&hand=${Uri.encode(meta.handedness)}" +
                "&goal=${Uri.encode(meta.goal)}" +
                "&lang=${Uri.encode(meta.language)}" +
                "&detail=${Uri.encode(meta.detailMode)}"
}
