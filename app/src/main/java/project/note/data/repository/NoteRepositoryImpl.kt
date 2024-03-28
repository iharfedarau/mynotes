package project.note.data.repository

import kotlinx.coroutines.flow.Flow
import project.note.BuildConfig
import project.note.data.Note
import project.note.data.db.NoteDao
import project.note.data.network.NoteService
import project.note.domain.repository.NoteRepository

class NoteRepositoryImpl(private val noteService: NoteService,
                     private val noteDao: NoteDao
): NoteRepository {

    override fun allNotes(): Flow<List<Note>> {
        return noteDao.getNotes()
    }

    override  suspend fun refreshNotes() {
        if (BuildConfig.IS_NETWORK_SERVICE_AVAILEBLE) {
            try {
                noteDao.insertAllNote(noteService.getNotes())
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun insert(note: Note): Note {
        if (BuildConfig.IS_NETWORK_SERVICE_AVAILEBLE) {
            try {
                noteService.insert(note)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        return  Note(note.title, note.content, noteDao.insertNote(note))
    }

    override suspend fun delete(id: Long) {
        if (BuildConfig.IS_NETWORK_SERVICE_AVAILEBLE) {
            try {
                noteService.delete(id)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        noteDao.delete(id)
    }

    override suspend fun update(note: Note) {
        if (BuildConfig.IS_NETWORK_SERVICE_AVAILEBLE) {
            try {
                noteService.update(note)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        noteDao.update(note.id, note.title, note.content)
    }
}