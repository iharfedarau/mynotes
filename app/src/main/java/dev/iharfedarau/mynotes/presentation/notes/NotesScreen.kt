package dev.iharfedarau.mynotes.presentation.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.iharfedarau.mynotes.domain.repository.Note
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import dev.iharfedarau.mynotes.BuildConfig
import dev.iharfedarau.mynotes.R
import dev.iharfedarau.mynotes.presentation.dialogs.CustomAlertDialog
import java.util.Calendar

@Composable
fun NotesScreen(
    openNote: (noteId: Long) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val items = listOf(Icons.Default.Info)
    val selectedItem = remember { mutableStateOf(items[0]) }

    LaunchedEffect(key1 = viewModel.state.insertedNoteId) {
        viewModel.state.insertedNoteId?.let {
            scope.launch {
                openNote(it)
            }
        }
    }

    if (viewModel.state.inProgress) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    } else {
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
                            viewModel.onUiAction(
                                NotesAction.Insert(
                                    Note(
                                        "Unknown",
                                        "",
                                        Calendar.getInstance().timeInMillis
                                    )
                                )
                            )
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
                                onClick = openNote,
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
                                viewModel.onUiAction(NotesAction.Delete(noteToDelete!!))
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
}