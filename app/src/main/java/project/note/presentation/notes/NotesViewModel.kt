package project.note.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import project.note.domain.repository.Note
import project.note.domain.repository.NoteRepository
import project.note.domain.alarm.AlarmItem
import project.note.domain.alarm.AlarmScheduler
import project.note.domain.utils.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    val allNotes = repository.allNotes()

    fun insert(note: Note, callback: (note: Note) -> Unit) = viewModelScope.launch {
        callback(repository.insert(note))
    }

    fun delete(note: Note) = viewModelScope.launch {
        note.id?.let {
            repository.delete(it)
        }

        note.alarmDate?.let {
            alarmScheduler.cancel(AlarmItem(it.toLocalDateTime(), note.alarmMessage))
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