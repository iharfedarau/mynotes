package project.note.presentation.editnote

import androidx.compose.ui.text.input.TextFieldValue
import project.note.domain.alarm.AlarmItem

sealed interface EditNoteAction {
    data class SetAlarmAction(val item: AlarmItem?): EditNoteAction
    data class SetTitleAction(val title: String): EditNoteAction
    data class SetContentAction(val content: TextFieldValue): EditNoteAction
    data object DeleteAction : EditNoteAction
    data object SaveAction : EditNoteAction
    data object UndoAction: EditNoteAction
    data object RedoAction: EditNoteAction
    data object GoBack: EditNoteAction
}
