package project.note.domain.alarm

import java.time.LocalDateTime

data class AlarmItem(
    val date : LocalDateTime,
    val message : String? = null)
