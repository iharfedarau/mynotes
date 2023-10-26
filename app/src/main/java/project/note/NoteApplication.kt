package project.note

import android.app.Application
import project.note.data.NoteRepository
import project.note.data.NoteRoomDatabase

class NoteApplication: Application() {
    private val database by lazy { NoteRoomDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }
}