package com.aaa.gptenniscoach.feature.sessiondetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen displaying detailed information about a specific analysis session.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    vm: SessionDetailViewModel,
    onBack: () -> Unit,
    onOpenResults: (String) -> Unit
) {
    val detail by vm.detail.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (detail == null) {
                Text("Session not found.")
                return@Column
            }

            Text("ID: ${detail!!.id}")
            Text("Video mode: ${detail!!.videoStorageMode}")
            Text("Video path: ${detail!!.videoLocalPath ?: "(none)"}")

            Button(onClick = { onOpenResults(detail!!.id) }) {
                Text("Open Results")
            }

            HorizontalDivider()

            Button(
                onClick = { vm.delete(onDone = onBack) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Session")
            }

            HorizontalDivider()
            /*
            Text(
                "Stored response JSON:",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = detail!!.responseJson,
                style = MaterialTheme.typography.bodySmall
            )
            */
        }
    }
}

