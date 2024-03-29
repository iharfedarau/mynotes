package project.note.presentation

import project.note.domain.Note

sealed interface UiState {
    data object InProgress: UiState
    data class ShowNoteView(val currentNote: Note): UiState
    data object ShowNotesView: UiState
}