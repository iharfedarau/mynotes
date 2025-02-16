package dev.iharfedarau.mynotes.presentation.editnote


data class EditNoteState(
    val editNote: EditNote? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val canSave: Boolean = false
)
