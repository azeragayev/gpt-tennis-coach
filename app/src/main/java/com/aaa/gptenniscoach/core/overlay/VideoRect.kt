package com.aaa.gptenniscoach.core.overlay

import androidx.compose.ui.geometry.Offset

/**
 * Represents the rectangular region where a video frame is displayed within a view.
 */
data class VideoRect(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float
) {
    /**
     * Converts frame coordinates to view coordinates based on video rect placement.
     */
    fun mapFrameToView(
        fx: Float,
        fy: Float,
        frameW: Float,
        frameH: Float
    ): Offset {

        val vx = left + (fx / frameW) * width
        val vy = top + (fy / frameH) * height

        return Offset(vx, vy)
    }

    companion object {
        /**
         * Computes the exact rectangle where the video frame is drawn
         * inside the view, assuming FIT_CENTER behavior.
         */
        /**
         * Computes the exact rectangle where the video frame is drawn inside the view, assuming FIT_CENTER behavior.
         */
        fun compute(
            viewW: Float,
            viewH: Float,
            frameW: Float,
            frameH: Float
        ): VideoRect {
            if (viewW <= 0f || viewH <= 0f || frameW <= 0f || frameH <= 0f) {
                return VideoRect(0f, 0f, viewW, viewH)
            }

            val viewAspect = viewW / viewH
            val frameAspect = frameW / frameH

            return if (frameAspect > viewAspect) {
                val scaledH = viewW / frameAspect
                VideoRect(
                    left = 0f,
                    top = (viewH - scaledH) / 2f,
                    width = viewW,
                    height = scaledH
                )
            } else {
                val scaledW = viewH * frameAspect
                VideoRect(
                    left = (viewW - scaledW) / 2f,
                    top = 0f,
                    width = scaledW,
                    height = viewH
                )
            }
        }
    }
}
