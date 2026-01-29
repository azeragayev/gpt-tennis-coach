package com.aaa.gptenniscoach.core.overlay

data class OverlayDto(
    val version: String,
    val coordSpace: String,
    val frameWidth: Int,
    val frameHeight: Int,
    val minVis: Float,
    val connections: List<Pair<Int, Int>>,
    val frames: List<OverlayFrameDto>,
)

data class OverlayFrameDto(
    val tMs: Long,
    val xyv: List<List<Double>>
)

data class FrameLandmark(
    val index: Int,
    val x: Float,
    val y: Float,
    val visibility: Float
)

data class FrameBone(
    val ax: Float,
    val ay: Float,
    val bx: Float,
    val by: Float
)
