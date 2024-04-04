package project.note.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.note.BuildConfig
import project.note.data.NoteDto
import project.note.data.db.NoteDao
import project.note.data.network.NoteService
import project.note.data.toNoteDto
import project.note.domain.Note
import project.note.domain.repository.NoteRepository

class NoteRepositoryImpl(private val noteService: NoteService,
                     private val noteDao: NoteDao
): NoteRepository {

    override fun allNotes(): Flow<List<Note>> {
        return noteDao.getAll().map { noteDtoList ->
            noteDtoList.map { noteDto ->
                Note(noteDto.title, noteDto.content, noteDto.id)
            }
        }
    }

    override suspend fun getById(id: Long): NoteDto? {
        return noteDao.getById(id)
    }

    override  suspend fun refreshNotes() {
        if (BuildConfig.isNetworkServiceAvailable) {
            try {
                noteDao.insert(noteService.getNotes())
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun insert(note: Note): Note {
        if (BuildConfig.isNetworkServiceAvailable) {
            try {
                noteService.insert(note.toNoteDto())
            } catch (e: Exception) {
                println(e.message)
            }
        }

        return  Note(note.title, note.content, noteDao.insert(note.toNoteDto()))
    }

    override suspend fun delete(id: Long) {
        try {
            if (BuildConfig.isNetworkServiceAvailable) {
                noteService.delete(id)
            }
            noteDao.delete(id)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}