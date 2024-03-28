package project.note.repository

import kotlinx.coroutines.flow.Flow
import project.note.BuildConfig
import project.note.database.Note
import project.note.database.NoteDao
import project.note.network.NoteService

class NoteRepository(private val noteService: NoteService,
                     private val noteDao: NoteDao
) {
    val allNotes: Flow<List<Note>> = noteDao.getNotes()

    suspend fun refreshNotes() {
        if (BuildConfig.IS_NETWORK_SERVICE_AVAILEBLE) {
            try {
                noteDao.insertAllNote(noteService.getNotes())
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
    suspend fun insert(note: Note): Note {
        if (BuildConfig.IS_NETWORK_SERVICE_AVAILEBLE) {
            try {
                noteService.insert(note)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        return  Note(note.title, note.content, noteDao.insertNote(note))
    }

    suspend fun delete(id: Long) {
        if (BuildConfig.IS_NETWORK_SERVICE_AVAILEBLE) {
            try {
                noteService.delete(id)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        noteDao.delete(id)
    }

    suspend fun update(note: Note) {
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