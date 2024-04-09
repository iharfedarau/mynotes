package project.note.presentation.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import project.note.domain.Note
import project.note.domain.repository.NoteRepository
import project.note.presentation.utils.UndoRedoStack
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {

    var alarmDate by mutableStateOf<Date?>(null)
        private set

    var note by mutableStateOf<Note?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var content by mutableStateOf(TextFieldValue("", TextRange(0)))
        private set

    val undoRedo = UndoRedoStack()

    init {
        val noteId = savedStateHandle.get<Long>("noteId")!!
        if (noteId > -1) {
            viewModelScope.launch {
                repository.getById(noteId)?.let { note ->
                    alarmDate = if (note.alarmDate != null) Date(note.alarmDate) else null
                    title = note.title
                    content = TextFieldValue(note.content, TextRange(note.content.length))
                    this@NoteViewModel.note = note

                    undoRedo.setInitialValue(note.content)
                    undoRedo.setListener { data ->
                        content = data
                    }
                }
            }
        }
    }

    fun updateAlarmDate(alarmDate: Date) {
        this.alarmDate = alarmDate
    }

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: TextFieldValue) {
        undoRedo.push(content)
    }

    fun delete() {
        viewModelScope.launch {
            note?.id?.let {
                repository.delete(it)
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            repository.insert(
                Note(
                    title =  title,
                    content = content.text,
                    modificationDate =  Calendar.getInstance().timeInMillis,
                    alarmDate= alarmDate?.toInstant()?.toEpochMilli(),
                    id = note?.id
                )
            )
        }
    }

    fun undo() {
        undoRedo.undo()
    }

    fun redo() {
        undoRedo.redo()
    }
}