package dev.iharfedarau.mynotes.presentation.notes

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import dev.iharfedarau.mynotes.domain.repository.Note
import dev.iharfedarau.mynotes.domain.repository.NoteRepository
import dev.iharfedarau.mynotes.domain.alarm.AlarmItem
import dev.iharfedarau.mynotes.domain.alarm.AlarmScheduler
import dev.iharfedarau.mynotes.domain.export.NotesExporter
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler,
    private val exporter: NotesExporter
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

    private fun export(absDirPath: Uri) {
        state = state.copy(inProgress = true)
        viewModelScope.launch {
            allNotes.firstOrNull()?.let { notes ->
                exporter.export(notes,absDirPath)
            }
            state = state.copy(inProgress = false)
        }
    }

    private fun import(absDirPath: Uri) {
        state = state.copy(inProgress = true)

        viewModelScope.launch {
            allNotes.firstOrNull()?.let { notes ->
                for (note in notes) {
                    note.alarmDate?.let {
                        alarmScheduler.cancel(AlarmItem(it, note.alarmMessage))
                    }
                    val noteId = note.id
                    noteId?.let {
                        repository.delete(noteId)
                    }
                }
            }

            exporter.import(absDirPath)?.let {
                for (note in it) {
                    repository.insert(note)
                    note.alarmDate?.let {
                        alarmScheduler.schedule(AlarmItem(it, note.alarmMessage))
                    }
                }
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

            is NotesAction.Export -> {
                export(action.dirPath)
            }

            is NotesAction.Import -> {
                import(action.dirPath)
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