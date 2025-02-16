package dev.iharfedarau.mynotes.presentation.notes

data class NotesState(val inProgress: Boolean = false, val insertedNoteId: Long? = null)
