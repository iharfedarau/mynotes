package project.note.domain.repository

import kotlinx.coroutines.flow.Flow
import project.note.data.Note

interface NoteRepository {
    fun  allNotes(): Flow<List<Note>>

    suspend fun refreshNotes()

    suspend fun insert(note: Note): Note

    suspend fun delete(id: Long)

    suspend fun update(note: Note)
}