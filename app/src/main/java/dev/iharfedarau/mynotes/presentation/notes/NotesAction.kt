package dev.iharfedarau.mynotes.presentation.notes

import android.net.Uri
import dev.iharfedarau.mynotes.domain.repository.Note

sealed interface NotesAction {
    data class Insert(val note: Note): NotesAction
    data class Delete(val note: Note): NotesAction
    data class Export(val dirPath: Uri): NotesAction
    data class Import(val dirPath: Uri): NotesAction
}