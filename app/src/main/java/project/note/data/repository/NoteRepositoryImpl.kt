package project.note.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.note.BuildConfig
import project.note.data.db.NoteDao
import project.note.data.network.NoteService
import project.note.data.toNote
import project.note.data.toNoteDto
import project.note.domain.alarm.AlarmItem
import project.note.domain.repository.Note
import project.note.domain.repository.NoteRepository
import project.note.domain.utils.toLocalDateTime

class NoteRepositoryImpl(private val noteService: NoteService,
                     private val noteDao: NoteDao
): NoteRepository {

    override fun allNotes(): Flow<List<Note>> {
        return noteDao.getAll().map { noteDtoList ->
            noteDtoList.map { noteDto ->
                Note(noteDto.title, noteDto.content, noteDto.modificationDate, noteDto.alarmDate, noteDto.alarmMessage, noteDto.id)
            }
        }
    }

    override suspend fun getAllAlarms(): List<AlarmItem> {
        val result = mutableListOf<AlarmItem>()
        noteDao.getAlarms().forEach {
            if (it.alarmDate != null) {
                result.add(AlarmItem(it.alarmDate.toLocalDateTime(), it.alarmMessage))
            }
        }
        return result
    }

    override suspend fun getById(id: Long): Note? {
        return noteDao.getById(id)?.toNote()
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

        return note.copy( id = noteDao.insert(note.toNoteDto()) )
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