package project.note.data.network

import project.note.domain.repository.Note
import java.io.Serializable

data class NoteDto (
    val title: String,
    val content: String,
    val modificationDate: Long,
    val alarmDate: Long? = null,
    val alarmMessage: String? = null,
    val id: Long? = null): Serializable

fun Note.toNoteDto(): NoteDto {
    return NoteDto(title, content, modificationDate, alarmDate, alarmMessage, id)
}

fun NoteDto.toNote(): Note {
    return Note(title, content, modificationDate, alarmDate, alarmMessage, id)
}