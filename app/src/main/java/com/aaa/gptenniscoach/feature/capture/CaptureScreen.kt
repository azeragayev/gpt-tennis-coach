package com.aaa.gptenniscoach.feature.capture

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaa.gptenniscoach.data.repo.PlayerMeta

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Screen for capturing tennis swing videos with player metadata configuration.
 */
@Composable
fun CaptureScreen(
    onStartAnalyze: (Uri, PlayerMeta) -> Unit,
    onOpenHistory: () -> Unit,
    onPreviewVideo: (Uri) -> Unit,   // ADD
    vm: CaptureViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var captureUri by remember { mutableStateOf<Uri?>(null) }

    /* ---------- PICK VIDEO ---------- */

    val pickLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedVideoUri = uri
    }

    /* ---------- CAPTURE VIDEO ---------- */

    val captureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        val uri = captureUri
        if (success && uri != null) {
            selectedVideoUri = uri
        }
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                val uri = createVideoUri(context)
                captureUri = uri
                captureLauncher.launch(uri)
            }
        }

    /* ---------- UI ---------- */

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Tennis Coach") },
                actions = {
                    TextButton(onClick = onOpenHistory) {
                        Text("History")
                    }
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

            Text("Pick or capture a short video (3–8 seconds) of a single stroke.")

            /* ---------- PLAYER META ---------- */

            Card {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DropdownRow("Level", state.level, CaptureOptions.levels, vm::updateLevel)
                    DropdownRow("Handedness", state.handedness, CaptureOptions.handedness, vm::updateHand)
                    DropdownRow("Goal", state.goal, CaptureOptions.goals, vm::updateGoal)
                    DropdownRow("Language", state.language, CaptureOptions.languages, vm::updateLang)
                    DropdownRow("Detail", state.detailMode, CaptureOptions.detailModes, vm::updateDetail)
                }
            }

            /* ---------- ACTIONS ---------- */

            Button(
                onClick = { pickLauncher.launch(arrayOf("video/*")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pick Video")
            }

            OutlinedButton(
                onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Capture Video")
            }

            /* ---------- PREVIEW ---------- */

            selectedVideoUri?.let { uri ->

                Card {
                    @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(uri)
                                setOnPreparedListener { it.isLooping = true }
                                start()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onPreviewVideo(uri) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Preview Video")
                }

                Button(
                    onClick = { onStartAnalyze(uri, vm.toMeta()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Analyze Video")
                }

                OutlinedButton(
                    onClick = { selectedVideoUri = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }

            AssistChip(
                onClick = {},
                label = { Text("Tip: Stable camera, full body visible") }
            )
        }
    }
}

/* ---------- HELPERS ---------- */

private fun createVideoUri(context: android.content.Context): Uri {
    val values = ContentValues().apply {
        put(MediaStore.Video.Media.TITLE, "tennis_capture_${System.currentTimeMillis()}")
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
    }

    return context.contentResolver.insert(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        values
    )!!
}

/* ---------- DROPDOWN ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownRow(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, modifier = Modifier.width(90.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
