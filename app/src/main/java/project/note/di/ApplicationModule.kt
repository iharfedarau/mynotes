package project.note.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import project.note.database.NoteRoomDatabase
import project.note.network.NoteService
import project.note.repository.NoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class  ApplicationModule {
    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext context: Context): NoteRepository {
        return NoteRepository(NoteService.getService(), NoteRoomDatabase.getDatabase(context).noteDao())
    }
}