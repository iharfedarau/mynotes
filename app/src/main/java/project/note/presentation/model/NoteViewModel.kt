package project.note.presentation.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import project.note.data.Note
import project.note.domain.repository.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    var insertedNote: MutableLiveData<Note> = MutableLiveData()

    val allNotes = repository.allNotes().asLiveData()

    fun insert(note: Note) = viewModelScope.launch {
        insertedNote.postValue(repository.insert(note))
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