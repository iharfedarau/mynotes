package project.note.presentation.editnote

sealed interface CustomBottomSheetAction {
    data object Delete: CustomBottomSheetAction
    data object SetAlarm: CustomBottomSheetAction
    data object Dismiss: CustomBottomSheetAction
}