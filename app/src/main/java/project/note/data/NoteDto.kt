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
    @PrimaryKey(autoGenerate = true) val id: Long = 0): Serializable

fun NoteDto.toNote(): Note {
    return Note(title, content, id)
}

fun Note.toNoteDto(): NoteDto {
    return NoteDto(title, content, id)
}