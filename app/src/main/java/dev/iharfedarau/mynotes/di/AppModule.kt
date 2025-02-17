package dev.iharfedarau.mynotes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.iharfedarau.mynotes.data.db.NoteRoomDatabase
import dev.iharfedarau.mynotes.data.network.NoteService
import dev.iharfedarau.mynotes.data.repository.NoteRepositoryImpl
import dev.iharfedarau.mynotes.domain.repository.NoteRepository
import dev.iharfedarau.mynotes.domain.alarm.AlarmScheduler
import dev.iharfedarau.mynotes.data.alarm.AlarmSchedulerImpl
import dev.iharfedarau.mynotes.data.exporter.JsonNotesExporter
import dev.iharfedarau.mynotes.domain.export.NotesExporter
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

    @Provides
    @Singleton
    fun provideExporter(): NotesExporter {
        return JsonNotesExporter()
    }
}