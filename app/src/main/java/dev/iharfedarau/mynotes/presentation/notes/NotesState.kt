package dev.iharfedarau.mynotes.presentation.notes

import dev.iharfedarau.mynotes.domain.repository.Note

data class NotesState(val notes: List<Note> = emptyList(),
                      val inProgress: Boolean = false,
                      val insertedNoteId: Long? = null)
