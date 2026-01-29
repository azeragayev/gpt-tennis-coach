package com.aaa.gptenniscoach.feature.analyze

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen displaying real-time analysis progress with status and error handling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeScreen(
    vm: AnalyzeViewModel,
    onDone: (String) -> Unit,
    onCancel: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.doneSessionId) {
        state.doneSessionId?.let(onDone)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Analyzing") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(state.stage, style = MaterialTheme.typography.titleMedium)

            val p = state.progress
            if (p != null) {
                LinearProgressIndicator(progress = { p }, modifier = Modifier.fillMaxWidth())
                Text("${(p * 100).toInt()}%")
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let { err ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Analysis failed", style = MaterialTheme.typography.titleSmall)
                        Text(err)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.retryable) {
                        Button(onClick = { vm.start() }) { Text("Retry") }
                    }
                    OutlinedButton(onClick = onCancel) { Text("Back") }
                }
            } ?: run {
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}
