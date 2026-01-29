package com.aaa.gptenniscoach.core.overlay

import com.aaa.gptenniscoach.data.api.dto.OverlayDataDto

/**
 * Converts OverlayDataDto from API response to OverlayDto for rendering.
 */
fun OverlayDataDto.toOverlayDto(): OverlayDto {
    return OverlayDto(
        version = version,
        coordSpace = coordSpace,
        frameWidth = 0,     // injected later from video
        frameHeight = 0,    // injected later from video
        minVis = minVis.toFloat(),   // ✅ FIX
        connections = connections.map { it[0] to it[1] },
        frames = frames.map { f ->
            OverlayFrameDto(
                tMs = f.tMs,
                xyv = f.xyv
            )
        }
    )
}
