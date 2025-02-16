package dev.iharfedarau.mynotes.presentation.notes

import dev.iharfedarau.mynotes.domain.repository.Note

sealed interface NotesAction {
    data class Insert(val note: Note): NotesAction
    data class Delete(val note: Note): NotesAction
}