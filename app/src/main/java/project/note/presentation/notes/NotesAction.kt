package project.note.presentation.notes

import project.note.domain.repository.Note

sealed interface NotesAction {
    data class Insert(val note: Note): NotesAction
    data class Delete(val note: Note): NotesAction
}