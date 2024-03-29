package project.note.domain

data class Note(val title: String,
                val content: String,
                val id: Long = 0)
