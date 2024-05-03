package project.note.presentation.editnote

sealed class CustomBottomSheetAction {
    data object Delete: CustomBottomSheetAction()
    data object SetAlarm: CustomBottomSheetAction()
    data object Dismiss: CustomBottomSheetAction()
}