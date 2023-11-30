package project.note

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import project.note.repository.NoteRepository
import project.note.database.NoteRoomDatabase
import project.note.network.NoteService

@HiltAndroidApp
class NoteApplication: Application() {
    private val database by lazy { NoteRoomDatabase.getDatabase(this) }
    private val service by lazy { NoteService.getService() }

    val repository by lazy { NoteRepository(service, database.noteDao()) }
}