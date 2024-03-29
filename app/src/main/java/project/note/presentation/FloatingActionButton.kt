package project.note.presentation

import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun FloatingActionButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text("New") },
        onClick = {
            onClick()
        }
    )
}