package project.note.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun NoteAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = typography,
        shapes = shapes,
        content = content
    )
}