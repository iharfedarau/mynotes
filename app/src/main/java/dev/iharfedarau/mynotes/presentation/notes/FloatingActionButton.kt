package dev.iharfedarau.mynotes.presentation.notes


import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.iharfedarau.mynotes.R

@Composable
fun FloatingActionButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        content = { Text(stringResource(id = R.string.create_new_note)) },
        onClick = {
            onClick()
        }
    )
}