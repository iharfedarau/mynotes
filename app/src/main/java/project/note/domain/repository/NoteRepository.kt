package project.note.domain.repository

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun  allNotes(): Flow<List<Note>>

    suspend fun getById(id: Long): Note?

    suspend fun refreshNotes()

    suspend fun insert(note: Note): Note

    suspend fun delete(id: Long)
}