package project.note.presentation.editnote


data class EditNoteState(
    val editNoteItem: EditNoteItem = EditNoteItem(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val canSave: Boolean = false
)
