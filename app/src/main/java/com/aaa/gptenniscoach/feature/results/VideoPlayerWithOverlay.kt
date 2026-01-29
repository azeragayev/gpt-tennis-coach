package com.aaa.gptenniscoach.feature.results

import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.core.net.toUri
import kotlinx.coroutines.delay

import com.aaa.gptenniscoach.core.overlay.*

private const val TAG_VIDEO = "PlayerVideoSize"

/**
 * Composable displaying a video player with optional skeleton pose overlay visualization.
 */
@UnstableApi
@Composable
fun VideoPlayerWithOverlay(
    modifier: Modifier,
    sessionState: ResultsUiState,
    showOverlayToggle: Boolean = true
) {
    val context = LocalContext.current

    // ---------------- Player ----------------

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
        }
    }

    // ---------------- Video size logging ----------------

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                Log.d(
                    TAG_VIDEO,
                    "onVideoSizeChanged: " +
                            "w=${videoSize.width}, " +
                            "h=${videoSize.height}, " +
                            "rotation=${player.videoFormat?.rotationDegrees}"
                )
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    // ---------------- Media source ----------------

    val playbackUri: Uri? = remember(
        sessionState.videoStorageMode,
        sessionState.videoLocalPath,
        sessionState.videoContentUri
    ) {
        when (sessionState.videoStorageMode) {
            "APP_COPY" -> sessionState.videoLocalPath?.let { "file://$it".toUri() }
            else -> sessionState.videoContentUri?.toUri()
        }
    }

    LaunchedEffect(playbackUri) {
        playbackUri?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
        }
    }

    // ---------------- Overlay engine ----------------

    val baseOverlayDto: OverlayDto? =
        sessionState.response?.overlay?.toOverlayDto()

    val overlayEngine: OverlayEngine? =
        remember(baseOverlayDto) {
            baseOverlayDto?.let { OverlayEngine(it) }
        }

    // ---------------- Overlay toggle ----------------

    var userWantsOverlay by rememberSaveable { mutableStateOf(false) }
    val overlayVisible = userWantsOverlay && overlayEngine != null

    // ---------------- Playback position ----------------

    var posMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(33)
            posMs = player.currentPosition
        }
    }

    // ---------------- Video format & decoded frame size ----------------

    val videoFormat = player.videoFormat
    val rawW = videoFormat?.width?.toFloat()
    val rawH = videoFormat?.height?.toFloat()
    val rotation = videoFormat?.rotationDegrees ?: 0

    // IMPORTANT:
    // frameW / frameH must match the coordinate space of backend landmarks
    val frameW: Float?
    val frameH: Float?

    if (rawW != null && rawH != null) {
        if (rotation == 90 || rotation == 270) {
            frameW = rawH
            frameH = rawW
        } else {
            frameW = rawW
            frameH = rawH
        }
    } else {
        frameW = null
        frameH = null
    }

    // ---------------- UI ----------------

    Box(modifier = modifier) {

        AndroidView(
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    layoutParams =
                        android.widget.FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // -------- Toggle UI --------

        if (showOverlayToggle && overlayEngine != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row {
                    Text(
                        text = "Show Skeleton",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = userWantsOverlay,
                        onCheckedChange = { userWantsOverlay = it }
                    )
                }
            }
        }

        // ---------------- Overlay drawing ----------------

        if (overlayVisible && frameW != null && frameH != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {

                val videoRect = VideoRect.compute(
                    viewW = size.width,
                    viewH = size.height,
                    frameW = frameW,
                    frameH = frameH
                )

                val frame = overlayEngine.frameAt(posMs) ?: return@Canvas
                val landmarks = overlayEngine.landmarksForFrame(frame)
                val bones = overlayEngine.bonesForFrame(frame)

                // Draw bones
                for (b in bones) {
                    val a = videoRect.mapFrameToView(b.ax, b.ay, frameW, frameH)
                    val c = videoRect.mapFrameToView(b.bx, b.by, frameW, frameH)
                    drawLine(
                        color = Color.White,
                        start = a,
                        end = c,
                        strokeWidth = 4f
                    )
                }

                // Draw joints
                for (lm in landmarks) {
                    val o = videoRect.mapFrameToView(lm.x, lm.y, frameW, frameH)
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = o,
                        style = Stroke(width = 2f)
                    )
                }
            }
        }
    }
}
