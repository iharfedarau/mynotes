package dev.iharfedarau.mynotes.presentation.editnote

import androidx.compose.ui.text.input.TextFieldValue
import dev.iharfedarau.mynotes.domain.alarm.AlarmItem

data class EditNote(val title: String = "", val content: TextFieldValue = TextFieldValue(""), val alarmItem: AlarmItem? = null)