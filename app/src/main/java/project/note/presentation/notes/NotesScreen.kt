package project.note.presentation.notes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import project.note.domain.repository.Note
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import project.note.BuildConfig
import project.note.R
import project.note.presentation.dialogs.CustomAlertDialog
import java.util.Calendar

@Composable
fun NotesScreen(
    onItemClick: (note: Note) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val items = listOf(Icons.Default.Info)
    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item, contentDescription = null) },
                        label = { Text(stringResource(R.string.version) + ": " + BuildConfig.VERSION_NAME) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }


        },
        content = {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton {
                        viewModel.insert(Note("Unknown", "", Calendar.getInstance().timeInMillis)) {
                            onItemClick(it)
                        }
                    }
                },
                bottomBar = {
                    BottomBar(onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    })
                }
            ) { paddings ->
                val notes by viewModel.allNotes.collectAsState(initial = emptyList())
                var isRefreshing by remember { mutableStateOf(false) }
                var noteToDelete by remember {
                    mutableStateOf<Note?>(null)
                }

                PullToRefreshLazyColumn(
                    items = notes,
                    content = { note ->
                        NoteItem(note = note,
                            onClick = onItemClick,
                            onDelete = {
                                noteToDelete = it
                            })
                    },
                    modifier = Modifier.padding(paddings),
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            delay(1000L) // Simulated API call
                            isRefreshing = false
                        }
                    })

                if (noteToDelete != null) {
                    CustomAlertDialog(
                        onDismissRequest = {
                            noteToDelete = null
                        },
                        onConfirmation = {
                            viewModel.delete(noteToDelete!!)
                            noteToDelete = null
                        },
                        dialogTitle = stringResource(id = R.string.delete),
                        dialogText = stringResource(id = R.string.delete_note_confirmation),
                        icon = Icons.Default.Delete
                    )
                }
            }
        }
    )
}