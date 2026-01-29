package com.aaa.gptenniscoach.feature.results

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
//import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
//import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun ResultsScreen(
    vm: ResultsViewModel,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onOpenPreview: (String) -> Unit   //  new
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Session ID", style = MaterialTheme.typography.titleMedium)
            Text(state.sessionId)

            // Video with overlay
            VideoPlayerWithOverlay(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                sessionState = state
            )

            Button(
                onClick = { onOpenPreview(state.sessionId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Preview Video")
            }


            HorizontalDivider()

            Text("Coaching Feedback", style = MaterialTheme.typography.titleMedium)
            Text(state.feedbackText)

            HorizontalDivider()

            Text("Analysis Quality", style = MaterialTheme.typography.titleMedium)

            state.quality?.let {
                QualityCard(it)
            } ?: Text("Quality: not available")

            HorizontalDivider()

            Text("Overlay Summary", style = MaterialTheme.typography.titleMedium)
            Text(state.overlaySummary)


            /*
            Text("Full Backend JSON", style = MaterialTheme.typography.titleMedium)
            Text(
                text = state.rawJson,
                style = MaterialTheme.typography.bodySmall
            )
            */
        }
    }
}

/* ---------- QUALITY CARD ---------- */

@Composable
private fun QualityCard(quality: QualityUiModel) {
    var detailsExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Level: ${quality.level}")
            Text("Score: ${"%.3f".format(quality.score)}")
            Text("Frames used: ${quality.framesUsed.toInt()}")

            if (quality.warnings.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = "Warnings",
                    style = MaterialTheme.typography.titleSmall
                )
                quality.warnings.forEach {
                    Text("• $it")
                }
            }

            quality.details?.let {
                HorizontalDivider()

                Text(
                    text = if (detailsExpanded) "Hide details ▲" else "Show details ▼",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable {
                        detailsExpanded = !detailsExpanded
                    }
                )

                if (detailsExpanded) {
                    QualityDetails(it)
                }
            }
        }
    }
}

/* ---------- QUALITY DETAILS ---------- */

@Composable
private fun QualityDetails(details: JsonObject) {
    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        details.forEach { (key, value) ->
            JsonEntry(key, value, indent = 0)
        }
    }
}

/*
private fun formatJsonValue(value: JsonElement): String =
    when {
        value is JsonObject ->
            value.entries.joinToString(
                prefix = "{ ",
                postfix = " }"
            ) { (k, v) ->
                "$k=${v.jsonPrimitive.content}"
            }

        value.jsonPrimitive.isString ->
            value.jsonPrimitive.content

        else ->
            value.toString()
    }
*/
@Composable
private fun JsonEntry(
    key: String,
    value: kotlinx.serialization.json.JsonElement,
    indent: Int
) {
    val padding = 12.dp * indent

    when (value) {
        is kotlinx.serialization.json.JsonPrimitive -> {
            Text(
                text = "$key: ${value.content}",
                modifier = Modifier.padding(start = padding),
                style = MaterialTheme.typography.bodySmall
            )
        }

        is kotlinx.serialization.json.JsonArray -> {
            Text(
                text = "$key:",
                modifier = Modifier.padding(start = padding),
                style = MaterialTheme.typography.bodySmall
            )
            value.forEachIndexed { index, element ->
                JsonEntry(
                    key = "[$index]",
                    value = element,
                    indent = indent + 1
                )
            }
        }

        //is kotlinx.serialization.json.JsonObject -> {
        is JsonObject -> {
            Text(
                text = "$key:",
                modifier = Modifier.padding(start = padding),
                style = MaterialTheme.typography.bodySmall
            )
            value.forEach { (childKey, childValue) ->
                JsonEntry(
                    key = childKey,
                    value = childValue,
                    indent = indent + 1
                )
            }
        }
    }
}
