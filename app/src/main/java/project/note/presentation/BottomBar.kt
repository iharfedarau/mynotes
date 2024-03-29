package project.note.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

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