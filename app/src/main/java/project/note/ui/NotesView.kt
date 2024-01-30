package project.note.ui

import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import project.note.databinding.NotesLayoutBinding

@Composable
//https://developer.android.com/jetpack/compose/components/app-bars
fun NotesView(activity: MainActivity) {
    Scaffold(
        topBar = {
            var menuExpanded by remember {
                mutableStateOf(false)
            }

            TopAppBar(
                title = {
                    Text("Notes")
                },
                actions = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        // 6
                        DropdownMenuItem(
                            content = {
                                Text("Add")
                            },
                            onClick = {
                                activity.addNote()
                                menuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("Remove")
                            },
                            onClick = {
                                activity.removeNote()
                                menuExpanded = false
                            },
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { context ->
                val view = NotesLayoutBinding.inflate(LayoutInflater.from(context), null, false)
                activity.initializeNotesView(view)
                view.root
            },
            update = {
            }
        )
    }
}