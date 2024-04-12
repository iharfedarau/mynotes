package project.note.presentation.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun NoteAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = NoteAppTheme.typography,
        shapes = NoteAppTheme.shapes,
        colorScheme = NoteAppTheme.colors,
        content = content
    )
}

object NoteAppTheme {
    val typography: Typography
        get() = project.note.presentation.ui.typography

    val shapes: Shapes
        get() = project.note.presentation.ui.shapes

    val colors: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
}