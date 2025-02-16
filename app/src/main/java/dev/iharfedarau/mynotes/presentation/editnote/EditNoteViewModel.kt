package dev.iharfedarau.mynotes.presentation.editnote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import dev.iharfedarau.mynotes.domain.repository.NoteRepository
import dev.iharfedarau.mynotes.domain.alarm.AlarmItem
import dev.iharfedarau.mynotes.domain.alarm.AlarmScheduler
import dev.iharfedarau.mynotes.domain.repository.Note
import dev.iharfedarau.mynotes.presentation.utils.EditNoteScreenRoute

import dev.iharfedarau.mynotes.presentation.utils.UndoRedoStack
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private lateinit var note: Note

    var state by mutableStateOf(EditNoteState())
        private set

    private val undoRedo = UndoRedoStack()

    init {

        val noteId = savedStateHandle.toRoute<EditNoteScreenRoute>().noteId
        if (noteId > -1) {
            viewModelScope.launch {
                repository.getById(noteId)?.let { localNote ->
                    note = localNote

                    state = state.copy(editNote = EditNote(
                        note.title,
                        TextFieldValue(note.content),
                        note.alarmDate?.let { date ->
                            AlarmItem(
                                date = date,
                                message = note.alarmMessage
                            )
                        })
                    )

                    undoRedo.setInitialValue(note.content)
                    undoRedo.addListener { content ->
                        state = state.copy(
                            editNote = state.editNote?.copy(content = content),
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
            note.id?.let {
                repository.delete(it)
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val editNoteItem = state.editNote
            if (editNoteItem != null) {
                val alarmItemRef = note.alarmDate?.let { date ->
                    AlarmItem(date, note.alarmMessage)
                }

                val noteToSave = note.copy(
                    title = editNoteItem.title,
                    content = editNoteItem.content.text,
                    modificationDate = Calendar.getInstance().timeInMillis,
                    alarmDate = editNoteItem.alarmItem?.date,
                    alarmMessage = editNoteItem.alarmItem?.message)

                repository.insert(noteToSave)

                if (editNoteItem.alarmItem != alarmItemRef) {
                    editNoteItem.alarmItem?.let(alarmScheduler::schedule)
                    alarmItemRef?.let(alarmScheduler::cancel)
                }
            }
        }
    }

    fun onUiAction(action: EditNoteAction) {
        when (action) {
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
                state = state.copy(editNote = state.editNote?.copy(alarmItem = action.item))
                state = state.copy(canSave = true)
            }

            is EditNoteAction.SetContentAction -> {
                if (action.content.text != state.editNote?.content?.text) {
                    state = state.copy(canSave = true)
                }

                undoRedo.push(action.content)
            }

            is EditNoteAction.SetTitleAction -> {
                state = state.copy(editNote = state.editNote?.copy(title = action.title))
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