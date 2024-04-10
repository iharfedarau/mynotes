package project.note.presentation.alarm

import java.time.LocalDateTime

data class AlarmItem(
    val date : LocalDateTime,
    val message : String? = null)
