package project.note.presentation.editnote

import androidx.compose.ui.text.input.TextFieldValue
import project.note.domain.alarm.AlarmItem

data class EditNote(val title: String = "", val content: TextFieldValue = TextFieldValue(""), val alarmItem: AlarmItem? = null)