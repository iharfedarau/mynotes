package project.note.presentation.notes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun BottomBar(onMenuClick: () -> Unit) {
    BottomAppBar {
        IconButton(onClick = {
            onMenuClick()
        }) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.weight(1.0f)) // fill height with spacer

        IconButton(onClick = {
        }) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
            )
        }
    }
}