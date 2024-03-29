package project.note.presentation

import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import project.note.data.Note

@Composable
fun FloatingActionButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text("New") },
        onClick = {
            onClick()
        }
    )
}