package project.note.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import project.note.database.Note
import project.note.repository.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    val allNotes: LiveData<List<Note>> = repository.allNotes.asLiveData()

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun delete(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    init {
        viewModelScope.launch {
            repository.refreshNotes()
        }
    }
}