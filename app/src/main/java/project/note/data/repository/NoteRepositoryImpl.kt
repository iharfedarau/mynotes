package project.note.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.note.BuildConfig
import project.note.data.db.NoteDao
import project.note.data.network.NoteService
import project.note.data.toNoteDto
import project.note.domain.Note
import project.note.domain.repository.NoteRepository

class NoteRepositoryImpl(private val noteService: NoteService,
                     private val noteDao: NoteDao
): NoteRepository {

    override fun allNotes(): Flow<List<Note>> {
        return noteDao.getNotes().map { noteDtoList ->
            noteDtoList.map { noteDto ->
                Note(noteDto.title, noteDto.content, noteDto.id)
            }
        }
    }

    override  suspend fun refreshNotes() {
        if (BuildConfig.isNetworkServiceAvailable) {
            try {
                noteDao.insertAllNote(noteService.getNotes())
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

        return  Note(note.title, note.content, noteDao.insertNote(note.toNoteDto()))
    }

    override suspend fun delete(id: Long) {
        if (BuildConfig.isNetworkServiceAvailable) {
            try {
                noteService.delete(id)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        noteDao.delete(id)
    }

    override suspend fun update(note: Note) {
        if (BuildConfig.isNetworkServiceAvailable) {
            try {
                noteService.update(note.toNoteDto())
            } catch (e: Exception) {
                println(e.message)
            }
        }

        noteDao.update(note.id, note.title, note.content)
    }
}