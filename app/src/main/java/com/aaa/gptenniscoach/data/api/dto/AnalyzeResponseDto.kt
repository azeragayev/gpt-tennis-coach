package com.aaa.gptenniscoach.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class AnalyzeResponseDto(
    val sessionId: String,
    val metrics: Map<String, Double> = emptyMap(),
    val feedback: FeedbackDto? = null,
    val quality: QualityDto? = null,
    val overlay: OverlayDataDto? = null
)

/* ---------- FEEDBACK ---------- */

@Serializable
data class FeedbackDto(
    val headline: String? = null,
    val focusPoints: List<FocusPointDto> = emptyList(),
    val drills: List<DrillDto> = emptyList(),
    val safetyNotes: String? = null
)

@Serializable
data class FocusPointDto(
    val title: String,
    val currentIssue: String,
    val target: String,
    val feelCue: String
)

@Serializable
data class DrillDto(
    val title: String,
    val description: String
)

/* ---------- QUALITY ---------- */

@Serializable
data class QualityDto(
    val level: String,
    val score: Double,
    val framesUsed: Double,
    val warnings: List<String> = emptyList(),

    // ✅ preserves full backend structure
    val details: JsonObject? = null
)

/* ---------- OVERLAY ---------- */

@Serializable
data class OverlayDataDto(
    val version: String,
    val fps: Double,

    @SerialName("coord_space")
    val coordSpace: String,

    @SerialName("landmark_ids")
    val landmarkIds: List<Int>,

    val connections: List<List<Int>>,

    @SerialName("window_start_ms")
    val windowStartMs: Long,

    @SerialName("window_end_ms")
    val windowEndMs: Long,

    @SerialName("min_vis")
    val minVis: Double,

    val frames: List<OverlayFrameDto> = emptyList()
)

@Serializable
data class OverlayFrameDto(
    @SerialName("t_ms")
    val tMs: Long,

    val xyv: List<List<Double>>
)
