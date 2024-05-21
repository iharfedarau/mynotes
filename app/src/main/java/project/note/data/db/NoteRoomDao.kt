package project.note.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteRoomDao {
    @Query("SELECT * FROM note_table ORDER BY modification_date DESC")
    fun getAll(): Flow<List<NoteDao>>

    @Query("SELECT * FROM note_table WHERE alarm_date IS NOT NULL ORDER BY modification_date DESC")
    suspend fun getAlarms(): List<NoteDao>

    @Query("SELECT * FROM note_table WHERE id=:id")
    suspend fun getById(id: Long): NoteDao?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteDao): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: List<NoteDao>)

    @Query("DELETE FROM note_table WHERE id=:id")
    suspend fun delete(id: Long)
}