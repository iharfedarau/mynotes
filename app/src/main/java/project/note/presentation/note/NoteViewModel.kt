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
import project.note.domain.repository.Note
import project.note.domain.repository.NoteRepository
import project.note.domain.alarm.AlarmItem
import project.note.domain.alarm.AlarmScheduler

import project.note.presentation.utils.UndoRedoStack
import project.note.presentation.utils.toLocalDateTime
import project.note.presentation.utils.toLong
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    var alarmItem by mutableStateOf<AlarmItem?>(null)
        private set

    private var alarmItemRef by mutableStateOf<AlarmItem?>(null)

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
                    note.alarmDate?.let { alarmDate ->
                        alarmItem = AlarmItem(
                            date = alarmDate.toLocalDateTime(),
                            message = note.alarmMessage
                        )
                        alarmItemRef = alarmItem
                    }

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

    fun updateAlarm(alarmItem: AlarmItem?) {
        this.alarmItem = alarmItem
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
                    title = title,
                    content = content.text,
                    modificationDate = Calendar.getInstance().timeInMillis,
                    alarmDate = alarmItem?.date?.toLong(),
                    alarmMessage = alarmItem?.message,
                    id = note?.id
                )
            )

            if (alarmItem != alarmItemRef) {
                alarmItem?.let(alarmScheduler::schedule)
                alarmItemRef?.let(alarmScheduler::cancel)
            }
        }
    }

    fun undo() {
        undoRedo.undo()
    }

    fun redo() {
        undoRedo.redo()
    }
}