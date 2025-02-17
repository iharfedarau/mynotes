package dev.iharfedarau.mynotes.domain.export

import android.net.Uri
import dev.iharfedarau.mynotes.domain.repository.Note


interface NotesExporter {
    fun export(notes: List<Note>, absDirPath: Uri)
    fun import(absDirPath: Uri): List<Note>?
}