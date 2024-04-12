package project.note.presentation.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun NoteAppTheme(darkTheme: Boolean = isSystemInDarkTheme(),
                 content:@Composable () -> Unit) {

    val colors = if (darkTheme) {
        NoteAppTheme.colors
    } else {
        NoteAppTheme.colors
    }

    MaterialTheme(
        typography = NoteAppTheme.typography,
        shapes = NoteAppTheme.shapes,
        colorScheme = colors,
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