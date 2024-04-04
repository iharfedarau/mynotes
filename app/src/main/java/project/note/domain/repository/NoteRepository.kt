package project.note.domain.repository

import kotlinx.coroutines.flow.Flow
import project.note.data.NoteDto
import project.note.domain.Note

interface NoteRepository {
    fun  allNotes(): Flow<List<Note>>

    suspend fun getById(id: Long): NoteDto?

    suspend fun refreshNotes()

    suspend fun insert(note: Note): Note

    suspend fun insert(notes: List<NoteDto>)

    suspend fun delete(id: Long)
}