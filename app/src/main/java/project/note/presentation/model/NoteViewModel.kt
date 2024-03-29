package project.note.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import project.note.domain.Note
import project.note.domain.repository.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    val allNotes = repository.allNotes()

    fun insert(note: Note, callback: (note: Note) -> Unit) = viewModelScope.launch {
        callback(repository.insert(note))
    }

    fun delete(id: Long) = viewModelScope.launch {
        repository.delete(id)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
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