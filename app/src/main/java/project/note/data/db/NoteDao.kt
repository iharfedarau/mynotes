package project.note.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import project.note.data.NoteDto

@Dao
interface NoteDao {
    @Query("SELECT * FROM note_table")
    fun getAll(): Flow<List<NoteDto>>

    @Query("SELECT * FROM note_table WHERE id=:id")
    suspend fun getById(id: Long): NoteDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteDto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: List<NoteDto>)

    @Query("DELETE FROM note_table WHERE id=:id")
    suspend fun delete(id: Long)
}