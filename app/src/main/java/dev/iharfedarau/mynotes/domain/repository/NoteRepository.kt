package dev.iharfedarau.mynotes.domain.repository

import kotlinx.coroutines.flow.Flow
import dev.iharfedarau.mynotes.domain.alarm.AlarmItem

interface NoteRepository {
    fun  allNotes(): Flow<List<Note>>

    suspend fun getAllAlarms(): List<AlarmItem>

    suspend fun getById(id: Long): Note?

    suspend fun refreshNotes()

    suspend fun insert(note: Note): Note

    suspend fun delete(id: Long)
}