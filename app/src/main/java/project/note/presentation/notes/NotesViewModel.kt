package project.note.presentation.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import project.note.domain.repository.Note
import project.note.domain.repository.NoteRepository
import project.note.domain.alarm.AlarmItem
import project.note.domain.alarm.AlarmScheduler
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    val allNotes = repository.allNotes()
    var state by mutableStateOf(NotesState())
        private set

    private fun insert(note: Note) {
        state = state.copy(inProgress = true)

        viewModelScope.launch {
            state = state.copy(insertedNoteId = repository.insert(note).id)
        }
    }

    private fun delete(note: Note) {
        state = state.copy(inProgress = true)

        viewModelScope.launch {
            note.id?.let {
                repository.delete(it)
            }

            note.alarmDate?.let {
                alarmScheduler.cancel(AlarmItem(it, note.alarmMessage))
            }

            state = state.copy(inProgress = false)
        }
    }

    fun onUiAction(action: NotesAction) {
        when (action) {
            is NotesAction.Insert -> {
                insert(action.note)
            }

            is NotesAction.Delete -> {
                delete(action.note)
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            repository.refreshNotes()
        }
    }

    init {
        refreshData()
    }
}