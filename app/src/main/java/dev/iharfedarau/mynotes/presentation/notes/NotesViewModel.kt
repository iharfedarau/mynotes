package dev.iharfedarau.mynotes.presentation.notes

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import dev.iharfedarau.mynotes.domain.repository.Note
import dev.iharfedarau.mynotes.domain.repository.NoteRepository
import dev.iharfedarau.mynotes.domain.alarm.AlarmItem
import dev.iharfedarau.mynotes.domain.alarm.AlarmScheduler
import dev.iharfedarau.mynotes.domain.export.NotesExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler,
    private val exporter: NotesExporter
) : ViewModel() {
    private val _state = MutableStateFlow(NotesState())
    val state = _state.asStateFlow()

    private fun insert(note: Note) {
        _state.update {
            it.copy(inProgress = true)
        }

        viewModelScope.launch {
            _state.update {
                it.copy(insertedNoteId = repository.insert(note).id)
            }
        }
    }

    private fun delete(note: Note) {
        _state.update {
            it.copy(inProgress = true)
        }

        viewModelScope.launch {
            note.id?.let {
                repository.delete(it)
            }

            note.alarmDate?.let {
                alarmScheduler.cancel(AlarmItem(it, note.alarmMessage))
            }

            _state.update {
                it.copy(inProgress = false)
            }
        }
    }

    private fun export(absDirPath: Uri) {
        _state.update {
            it.copy(inProgress = true)
        }

        viewModelScope.launch {
            _state.firstOrNull()?.notes?.let { notes ->
                exporter.export(notes,absDirPath)
            }

            _state.update {
                it.copy(inProgress = false)
            }
        }
    }

    private fun import(absDirPath: Uri) {
        _state.update {
            it.copy(inProgress = true)
        }

        viewModelScope.launch {
            _state.firstOrNull()?.notes?.let { notes ->
                for (note in notes) {
                    note.alarmDate?.let { alarmDate->
                        alarmScheduler.cancel(AlarmItem(alarmDate, note.alarmMessage))
                    }
                    val noteId = note.id
                    noteId?.let {
                        repository.delete(noteId)
                    }
                }
            }

            exporter.import(absDirPath)?.let { notes ->
                for (note in notes) {
                    repository.insert(note)
                    note.alarmDate?.let { alarmDate ->
                        alarmScheduler.schedule(AlarmItem(alarmDate, note.alarmMessage))
                    }
                }
            }

            _state.update {
                it.copy(inProgress = false)
            }
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
        viewModelScope.launch {
            repository.allNotes().collect { value ->
                _state.update {
                    it.copy(notes = value)
                }
            }
        }

        refreshData()
    }
}