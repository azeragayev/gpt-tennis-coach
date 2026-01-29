package com.aaa.gptenniscoach.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaa.gptenniscoach.data.repo.SessionRepository
import com.aaa.gptenniscoach.data.repo.SessionSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: SessionRepository   // make private
) : ViewModel() {

    val sessions: StateFlow<List<SessionSummary>> =
        repo.observeSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Deletes all stored analysis sessions from the database.
     */
    fun deleteAllSessions() {
        viewModelScope.launch {
            repo.deleteAllSessions()
        }
    }
}
