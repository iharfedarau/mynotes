package project.note.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import project.note.domain.Note
import java.io.Serializable

@Entity(tableName="note_table")
data class NoteDto (
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "modification_date") val modificationDate: Long,
    @ColumnInfo(name = "alarm_date") val alarmDate: Long? = null,
    @ColumnInfo(name = "alarm_message") val alarmMessage: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long? = null): Serializable

fun Note.toNoteDto(): NoteDto {
    return NoteDto(title, content, modificationDate, alarmDate, alarmMessage, id)
}

fun NoteDto.toNote(): Note {
    return Note(title, content, modificationDate, alarmDate, alarmMessage, id)
}