package project.note.presentation.note

sealed class CustomBottomSheetAction {
    data object Delete: CustomBottomSheetAction()
    data object SetAlarm: CustomBottomSheetAction()
    data object Dismiss: CustomBottomSheetAction()
}