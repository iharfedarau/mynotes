package dev.iharfedarau.mynotes.data.exporter

import android.content.Context
import android.net.Uri
import dev.iharfedarau.mynotes.domain.export.NotesExporter
import dev.iharfedarau.mynotes.domain.repository.Note
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class JsonNotesExporter(private val context: Context): NotesExporter {
    override fun export(notes: List<Note>, absDirPath: Uri) {
        context.contentResolver.openOutputStream(absDirPath)?.use { outputStream ->
            BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                writer.write(Json.encodeToString(notes))
            }
        }
    }

    override fun import(absDirPath: Uri): List<Note>? {
        var res: List<Note>? = null
        context.contentResolver.openInputStream(absDirPath)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                res = Json.decodeFromString<List<Note>>(reader.readText())
            }
        }
        return res
    }
}