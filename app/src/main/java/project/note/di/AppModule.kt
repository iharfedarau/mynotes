package project.note.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import project.note.data.db.NoteRoomDatabase
import project.note.data.network.NoteService
import project.note.data.repository.NoteRepositoryImpl
import project.note.domain.repository.NoteRepository
import project.note.presentation.alarm.AlarmScheduler
import project.note.presentation.alarm.AlarmSchedulerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class  AppModule {
    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext context: Context): NoteRepository {
        return NoteRepositoryImpl(NoteService.getService(), NoteRoomDatabase.getDatabase(context).noteDao())
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmSchedulerImpl(context)
    }
}