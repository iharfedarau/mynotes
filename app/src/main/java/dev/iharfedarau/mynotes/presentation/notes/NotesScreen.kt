package dev.iharfedarau.mynotes.presentation.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.iharfedarau.mynotes.R
import dev.iharfedarau.mynotes.domain.repository.Note
import dev.iharfedarau.mynotes.presentation.dialogs.CustomAlertDialog
import dev.iharfedarau.mynotes.presentation.notes.drawer.DrawerAction
import dev.iharfedarau.mynotes.presentation.utils.FilePicker
import dev.iharfedarau.mynotes.presentation.utils.FilePickerMode
import dev.iharfedarau.mynotes.presentation.utils.isExternalStorageAvailable
import dev.iharfedarau.mynotes.presentation.utils.isExternalStorageReadOnly
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun NotesScreen(
    openNote: (noteId: Long) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var showPicker by remember { mutableStateOf(false) }
    var pickerMode by remember { mutableStateOf(FilePickerMode.CreateFile) }

    FilePicker(showPicker, pickerMode) { mode, uri ->
        uri?.let {
            when (mode) {
                FilePickerMode.CreateFile -> {
                    viewModel.onUiAction(NotesAction.Export(uri))
                }

                FilePickerMode.OpenFile -> {
                    viewModel.onUiAction(NotesAction.Import(uri))
                }
            }
        }
        showPicker = false
    }

    val items = listOf(
        DrawerAction.Export {
            pickerMode = FilePickerMode.CreateFile
            showPicker = true
            scope.launch { drawerState.close() }

        },
        DrawerAction.Import{
            pickerMode = FilePickerMode.OpenFile
            showPicker = true
            scope.launch { drawerState.close() }
        },
        DrawerAction.About{
            scope.launch { drawerState.close() }
        }
    )
    val selectedItem = remember { mutableStateOf(items.last()) }

    val state = viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.value.insertedNoteId) {
        state.value.insertedNoteId?.let {
            scope.launch {
                openNote(it)
            }
        }
    }

    if (state.value.inProgress) {
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
                    items.forEach {
                        if ((it is DrawerAction.Export && state.value.notes.isNotEmpty() && isExternalStorageAvailable() && !isExternalStorageReadOnly()) ||
                            (it is DrawerAction.Import && isExternalStorageAvailable()) ||
                            it is DrawerAction.About
                        ) {
                            NavigationDrawerItem(
                                icon = { Icon(it.icon, contentDescription = null) },
                                label = { Text(stringResource(it.textResId) + if (it.extraText != null) ": ${it.extraText}" else "") },
                                selected = it == selectedItem.value,
                                onClick = {
                                    it.onClick()
                                    selectedItem.value = it
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
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
                    //val notes by viewModel.allNotes.collectAsState(initial = emptyList())
                    var isRefreshing by remember { mutableStateOf(false) }
                    var noteToDelete by remember {
                        mutableStateOf<Note?>(null)
                    }

                    PullToRefreshLazyColumn(
                        items = state.value.notes,
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