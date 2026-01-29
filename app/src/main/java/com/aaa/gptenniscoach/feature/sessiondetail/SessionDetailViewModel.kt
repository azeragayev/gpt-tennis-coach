package com.aaa.gptenniscoach.feature.sessiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaa.gptenniscoach.data.repo.SessionDetail
import com.aaa.gptenniscoach.data.repo.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val repo: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle["sessionId"])

    val detail: StateFlow<SessionDetail?> =
        repo.observeSession(sessionId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Deletes the current session and invokes callback when complete.
     */
    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.deleteSession(sessionId)
            onDone()
        }
    }
}

