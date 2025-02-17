package dev.iharfedarau.mynotes.data.exporter

import android.net.Uri
import dev.iharfedarau.mynotes.domain.export.NotesExporter
import dev.iharfedarau.mynotes.domain.repository.Note
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonNotesExporter: NotesExporter {
    override fun export(notes: List<Note>, absDirPath: Uri) {
        val serializedNotes = Json.encodeToString(notes)
    }

    override fun import(absDirPath: Uri): List<Note>? {
        val serializedNotes: String = ""
        serializedNotes?.let {
            return Json.decodeFromString<List<Note>>(it)
        }
        return null
    }
}