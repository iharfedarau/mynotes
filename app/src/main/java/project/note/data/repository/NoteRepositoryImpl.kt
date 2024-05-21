package project.note.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.note.BuildConfig
import project.note.data.db.NoteRoomDao
import project.note.data.db.toNote
import project.note.data.db.toNoteDao
import project.note.data.network.NoteService
import project.note.data.network.toNote
import project.note.data.network.toNoteDto
import project.note.domain.alarm.AlarmItem
import project.note.domain.repository.Note
import project.note.domain.repository.NoteRepository

class NoteRepositoryImpl(private val noteService: NoteService,
                     private val noteRoomDao: NoteRoomDao
): NoteRepository {

    override fun allNotes(): Flow<List<Note>> {
        return noteRoomDao.getAll().map { noteDaoList ->
            noteDaoList.map { noteDao ->
                Note(noteDao.title, noteDao.content, noteDao.modificationDate, noteDao.alarmDate, noteDao.alarmMessage, noteDao.id)
            }
        }
    }

    override suspend fun getAllAlarms(): List<AlarmItem> {
        val result = mutableListOf<AlarmItem>()
        noteRoomDao.getAlarms().forEach {
            if (it.alarmDate != null) {
                result.add(AlarmItem(it.alarmDate, it.alarmMessage))
            }
        }
        return result
    }

    override suspend fun getById(id: Long): Note? {
        return noteRoomDao.getById(id)?.toNote()
    }

    override  suspend fun refreshNotes() {
        if (BuildConfig.isNetworkServiceAvailable) {
            try {
                noteRoomDao.insert(noteService.getNotes().map {
                    it.toNote()
                }.map {
                    it.toNoteDao()
                })
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

        return note.copy( id = noteRoomDao.insert(note.toNoteDao()) )
    }

    override suspend fun delete(id: Long) {
        try {
            if (BuildConfig.isNetworkServiceAvailable) {
                noteService.delete(id)
            }
            noteRoomDao.delete(id)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}