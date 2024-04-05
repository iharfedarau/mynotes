package project.note.presentation.notes


import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FloatingActionButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        content = { Text("New") },
        onClick = {
            onClick()
        }
    )
}