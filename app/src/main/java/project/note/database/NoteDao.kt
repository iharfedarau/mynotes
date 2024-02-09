package project.note.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note_table")
    fun getNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNote(note: List<Note>)

    @Query("DELETE FROM note_table WHERE id=:id")
    suspend fun delete(id: Int)

    @Query("UPDATE note_table SET title=:title, content=:content WHERE id=:id")
    suspend fun update(id: Int, title: String, content: String)
}