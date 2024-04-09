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
import project.note.presentation.alarm.AlarmItem
import project.note.presentation.alarm.AlarmScheduler

import project.note.presentation.utils.UndoRedoStack
import project.note.presentation.utils.toLocalDateTime
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    var alarmDate by mutableStateOf<Long?>(null)
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
                    alarmDate = note.alarmDate
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

    fun updateAlarmDate(alarmDate: Long?) {
        if (alarmDate == null) {
            this.alarmDate?.let {
                alarmScheduler.cancel(AlarmItem(it.toLocalDateTime(), "!!!!!!!!!!"))
            }
        }

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
                    alarmDate= alarmDate,
                    id = note?.id
                )
            )
        }

        alarmDate?.let {
            alarmScheduler.schedule(AlarmItem(it.toLocalDateTime(), "!!!!!!!!!!"))
        }
    }

    fun undo() {
        undoRedo.undo()
    }

    fun redo() {
        undoRedo.redo()
    }
}