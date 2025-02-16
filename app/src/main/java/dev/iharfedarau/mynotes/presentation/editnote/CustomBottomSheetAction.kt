package dev.iharfedarau.mynotes.presentation.editnote

sealed interface CustomBottomSheetAction {
    data object Delete: CustomBottomSheetAction
    data object SetAlarm: CustomBottomSheetAction
    data object Dismiss: CustomBottomSheetAction
}