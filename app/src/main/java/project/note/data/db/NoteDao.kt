package project.note.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import project.note.domain.repository.Note
import java.io.Serializable

@Entity(tableName="note_table")
data class NoteDao (
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "modification_date") val modificationDate: Long,
    @ColumnInfo(name = "alarm_date") val alarmDate: Long? = null,
    @ColumnInfo(name = "alarm_message") val alarmMessage: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long? = null): Serializable

fun Note.toNoteDao(): NoteDao {
    return NoteDao(title, content, modificationDate, alarmDate, alarmMessage, id)
}

fun NoteDao.toNote(): Note {
    return Note(title, content, modificationDate, alarmDate, alarmMessage, id)
}