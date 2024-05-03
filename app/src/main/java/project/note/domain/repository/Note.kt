package project.note.domain.repository

data class Note(val title: String = "",
                val content: String = "",
                val modificationDate: Long = 0,
                val alarmDate: Long? = null,
                val alarmMessage: String? = null,
                val id: Long? = null)
