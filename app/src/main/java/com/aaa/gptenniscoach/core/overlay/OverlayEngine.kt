package com.aaa.gptenniscoach.core.overlay

/**
 * Engine for rendering skeleton overlay frames from analysis data.
 */
class OverlayEngine(
    private val overlay: OverlayDto
) {

    private val frames = overlay.frames

    /**
     * Returns the last frame whose timestamp is <= positionMs.
     * This guarantees stable overlays during playback.
     */
    /**
     * Returns the last frame whose timestamp is <= positionMs.
     * This guarantees stable overlays during playback.
     */
    fun frameAt(positionMs: Long): OverlayFrameDto? {
        if (frames.isEmpty()) return null

        var best: OverlayFrameDto? = null
        for (f in frames) {
            if (f.tMs <= positionMs) {
                best = f
            } else {
                break
            }
        }
        return best ?: frames.first()
    }

    /**
     * Returns landmarks already expressed in VIDEO PIXELS.
     * No normalization, no aspect math, no crop compensation.
     */
    /**
     * Returns landmarks already expressed in VIDEO PIXELS.
     * No normalization, no aspect math, no crop compensation.
     */
    fun landmarksForFrame(frame: OverlayFrameDto): List<FrameLandmark> {
        val out = ArrayList<FrameLandmark>(frame.xyv.size)

        frame.xyv.forEachIndexed { idx, p ->
            if (p.size < 2) return@forEachIndexed

            val xPx = p[0].toFloat()
            val yPx = p[1].toFloat()
            val vis = if (p.size >= 3) p[2].toFloat() else 1f

            // Visibility filtering is optional; keep it permissive
            if (vis < overlay.minVis) return@forEachIndexed

            out.add(
                FrameLandmark(
                    index = idx,
                    x = xPx,
                    y = yPx,
                    visibility = vis
                )
            )
        }

        return out
    }

    /**
     * Builds bones directly from pixel landmarks.
     */
    /**
     * Builds bones directly from pixel landmarks.
     */
    fun bonesForFrame(frame: OverlayFrameDto): List<FrameBone> {
        val lmByIndex = landmarksForFrame(frame).associateBy { it.index }
        val bones = ArrayList<FrameBone>(overlay.connections.size)

        for ((a, b) in overlay.connections) {
            val la = lmByIndex[a] ?: continue
            val lb = lmByIndex[b] ?: continue

            bones.add(
                FrameBone(
                    ax = la.x,
                    ay = la.y,
                    bx = lb.x,
                    by = lb.y
                )
            )
        }
        return bones
    }
}
