package dev.iharfedarau.mynotes.presentation.utils

import kotlinx.serialization.Serializable

@Serializable
object NotesScreenRoute

@Serializable
data class EditNoteScreenRoute(val noteId: Long)