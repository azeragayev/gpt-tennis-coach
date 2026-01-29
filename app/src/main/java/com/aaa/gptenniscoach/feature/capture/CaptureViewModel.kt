package com.aaa.gptenniscoach.feature.capture

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.aaa.gptenniscoach.data.repo.PlayerMeta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/* ---------- CONSTANT OPTION SETS ---------- */

object CaptureOptions {

    val levels = listOf("2.5", "3.0", "3.5", "4.0", "4.5", "5.0")
    val handedness = listOf("right", "left")
    val goals = listOf("consistency", "power", "spin", "accuracy")
    val languages = listOf("en", "az", "ru", "fr", "de", "es", "it", "tr", "uk", "ka")
    val detailModes = listOf("brief", "normal", "detailed")
}

/* ---------- CAMERA ---------- */

enum class CameraFacing {
    BACK,
    FRONT
}

enum class CaptureStage {
    IDLE,
    PREVIEW
}

/* ---------- UI STATE ---------- */

data class CaptureUiState(
    val level: String = CaptureOptions.levels.first(),
    val handedness: String = CaptureOptions.handedness.first(),
    val goal: String = CaptureOptions.goals.first(),
    val language: String = CaptureOptions.languages.first(),
    val detailMode: String = CaptureOptions.detailModes[1],

    val cameraFacing: CameraFacing = CameraFacing.BACK,
    val capturedVideoUri: Uri? = null,
    val stage: CaptureStage = CaptureStage.IDLE
)

/* ---------- VIEWMODEL ---------- */

@HiltViewModel
class CaptureViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CaptureUiState())
    val state: StateFlow<CaptureUiState> = _state

    /* --- meta updates --- */

    /**
     * Updates the player skill level selection.
     */
    fun updateLevel(v: String) = update { it.copy(level = v) }
    /**
     * Updates the player hand dominance selection.
     */
    fun updateHand(v: String) = update { it.copy(handedness = v) }
    /**
     * Updates the improvement goal selection.
     */
    fun updateGoal(v: String) = update { it.copy(goal = v) }
    /**
     * Updates the feedback language selection.
     */
    fun updateLang(v: String) = update { it.copy(language = v) }
    /**
     * Updates the analysis detail level selection.
     */
    fun updateDetail(v: String) = update { it.copy(detailMode = v) }

    /**
     * Sets the active camera facing direction (front or back).
     */
    fun setCameraFacing(facing: CameraFacing) =
        update { it.copy(cameraFacing = facing) }

    /**
     * Records the captured video URI and transitions to preview stage.
     */
    fun onVideoCaptured(uri: Uri) =
        update {
            it.copy(
                capturedVideoUri = uri,
                stage = CaptureStage.PREVIEW
            )
        }

    /**
     * Clears the captured video and returns to idle capture stage.
     */
    fun onRetake() =
        update {
            it.copy(
                capturedVideoUri = null,
                stage = CaptureStage.IDLE
            )
        }

    /* --- helpers --- */

    /**
     * Converts current UI state to PlayerMeta for analysis request.
     */
    fun toMeta(): PlayerMeta =
        PlayerMeta(
            level = _state.value.level,
            handedness = _state.value.handedness,
            goal = _state.value.goal,
            language = _state.value.language,
            detailMode = _state.value.detailMode
        )

    private inline fun update(block: (CaptureUiState) -> CaptureUiState) {
        _state.value = block(_state.value)
    }
}
