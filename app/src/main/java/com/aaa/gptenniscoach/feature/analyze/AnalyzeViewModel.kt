package com.aaa.gptenniscoach.feature.analyze

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaa.gptenniscoach.data.repo.AnalyzeProgress
import com.aaa.gptenniscoach.data.repo.PlayerMeta
import com.aaa.gptenniscoach.data.repo.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyzeUiState(
    val stage: String = "Preparing…",
    val progress: Float? = null,
    val error: String? = null,
    val retryable: Boolean = false,
    val doneSessionId: String? = null
)

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val repo: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uriStr: String = checkNotNull(savedStateHandle["uri"])
    private val level: String = checkNotNull(savedStateHandle["level"])
    private val hand: String = checkNotNull(savedStateHandle["hand"])
    private val goal: String = checkNotNull(savedStateHandle["goal"])
    private val lang: String = checkNotNull(savedStateHandle["lang"])
    private val detail: String = checkNotNull(savedStateHandle["detail"])

    private val uri: Uri = Uri.parse(uriStr)

    private val meta = PlayerMeta(
        level = level,
        handedness = hand,
        goal = goal,
        language = lang,
        detailMode = detail
    )

    private val _state = MutableStateFlow(AnalyzeUiState())
    val state: StateFlow<AnalyzeUiState> = _state

    init {
        start()
    }

    /**
     * Starts or restarts the video analysis process from the beginning.
     */
    fun start() {
        _state.value = AnalyzeUiState(stage = "Preparing…", progress = null)
        viewModelScope.launch {
            repo.analyzeFromContentUri(uri, meta).collect { p ->
                when (p) {
                    is AnalyzeProgress.Copying -> _state.value = _state.value.copy(stage = "Copying video into app storage…", progress = null)
                    is AnalyzeProgress.Uploading -> _state.value = _state.value.copy(stage = "Uploading…", progress = p.fraction)
                    is AnalyzeProgress.Analyzing -> _state.value = _state.value.copy(stage = "Analyzing…", progress = null)
                    is AnalyzeProgress.Success -> _state.value = _state.value.copy(stage = "Done.", doneSessionId = p.sessionId)
                    is AnalyzeProgress.Error -> _state.value = _state.value.copy(stage = "Error", error = p.message, retryable = p.retryable)
                }
            }
        }
    }
}

