package project.note.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    var returnedVal: MutableLiveData<Note> = MutableLiveData()

    val allNotes: LiveData<List<Note>> = repository.allNotes.asLiveData()

    fun insert(note: Note) = viewModelScope.launch {
        returnedVal.postValue(repository.insert(note))
    }

    fun delete(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshNotes()
        }
    }

    init {
        refreshData()
    }
}