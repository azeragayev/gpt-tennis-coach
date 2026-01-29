package com.aaa.gptenniscoach.feature.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.aaa.gptenniscoach.feature.results.ResultsViewModel
import com.aaa.gptenniscoach.feature.results.VideoPlayerWithOverlay

/**
 * Screen for previewing stored analysis video with optional skeleton overlay.
 */
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PreviewScreen(
    vm: ResultsViewModel,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()

    // Overlay is available only if analysis is completed
    val overlayAvailable = state.response?.overlay != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Fullscreen-ish video
            VideoPlayerWithOverlay(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                sessionState = state,
                showOverlayToggle = overlayAvailable
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
            }
        }
    }
}
