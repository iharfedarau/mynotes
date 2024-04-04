package project.note.presentation

import android.annotation.SuppressLint
import android.os.Parcelable
import project.note.domain.Note
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface UiState: Parcelable {
    @SuppressLint("ParcelCreator")
    data object InProgress: UiState
    @SuppressLint("ParcelCreator")
    data class ShowNoteView(val currentNote: Note): UiState
    @SuppressLint("ParcelCreator")
    data object ShowNotesView: UiState
}