package project.note.presentation.editnote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import project.note.domain.repository.NoteRepository
import project.note.domain.alarm.AlarmItem
import project.note.domain.alarm.AlarmScheduler
import project.note.domain.repository.Note
import project.note.domain.utils.toLocalDateTime
import project.note.domain.utils.toLong

import project.note.presentation.utils.UndoRedoStack
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private var noteId: Long

    var state by mutableStateOf(EditNoteState())
        private set

    private var alarmItemRef by mutableStateOf<AlarmItem?>(null)

    private val undoRedo = UndoRedoStack()

    init {
        noteId = savedStateHandle.get<Long>("noteId")!!
        if (noteId > -1) {
            viewModelScope.launch {
                repository.getById(noteId)?.let { note ->
                    state = state.copy(editNoteItem = EditNoteItem(
                        note.title,
                        TextFieldValue(note.content),
                        note.alarmDate?.let { date ->
                            AlarmItem(
                                date = date.toLocalDateTime(),
                                message = note.alarmMessage
                            )
                        }))

                    undoRedo.setInitialValue(note.content)
                    undoRedo.addListener { content ->
                        state = state.copy(
                            editNoteItem = state.editNoteItem.copy(content = content),
                            canUndo = undoRedo.canUndo,
                            canRedo = undoRedo.canRedo
                        )
                    }
                }
            }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            repository.delete(noteId)
        }
    }

    private fun save() {
        viewModelScope.launch {
            repository.insert(
                note = Note(
                    title = state.editNoteItem.title,
                    content = state.editNoteItem.content.text,
                    modificationDate = Calendar.getInstance().timeInMillis,
                    alarmDate = state.editNoteItem.alarmItem?.date?.toLong(),
                    alarmMessage = state.editNoteItem.alarmItem?.message,
                    id = noteId
                )
            )

            if (state.editNoteItem.alarmItem != alarmItemRef) {
                state.editNoteItem.alarmItem?.let(alarmScheduler::schedule)
                alarmItemRef?.let(alarmScheduler::cancel)
            }
        }
    }

    fun onUiAction(event: EditNoteAction) {
        when (event) {
            EditNoteAction.DeleteAction -> {
                delete()
            }

            EditNoteAction.RedoAction -> {
                undoRedo.redo()
                state = state.copy(canSave = true)
            }

            EditNoteAction.SaveAction -> {
                save()
                state = state.copy(canSave = false)
            }

            is EditNoteAction.SetAlarmAction -> {
                state = state.copy(editNoteItem = state.editNoteItem.copy(alarmItem = event.item))
                state = state.copy(canSave = true)
            }

            is EditNoteAction.SetContentAction -> {
                undoRedo.push(event.content)
                state = state.copy(canSave = true)
            }

            is EditNoteAction.SetTitleAction -> {
                state = state.copy(editNoteItem = state.editNoteItem.copy(title = event.title))
                state = state.copy(canSave = true)
            }

            EditNoteAction.UndoAction -> {
                undoRedo.undo()
                state = state.copy(canSave = true)
            }

            EditNoteAction.GoBack -> {
                // Screen caries about this
            }
        }
    }
}