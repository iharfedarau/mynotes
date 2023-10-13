package project.note.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getNotes()

    @WorkerThread
    suspend fun insert(note: Note) {
        noteDao.insertNote(note)
    }

    @WorkerThread
    suspend fun delete(id: Int) {
        noteDao.delete(id)
    }

    @WorkerThread
    suspend fun update(note: Note) {
        noteDao.update(note.id, note.title, note.content)
    }
}