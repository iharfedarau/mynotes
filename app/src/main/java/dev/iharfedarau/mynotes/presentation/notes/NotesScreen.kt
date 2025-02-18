package dev.iharfedarau.mynotes.presentation.notes

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.hilt.navigation.compose.hiltViewModel
import dev.iharfedarau.mynotes.R
import dev.iharfedarau.mynotes.domain.repository.Note
import dev.iharfedarau.mynotes.presentation.dialogs.CustomAlertDialog
import dev.iharfedarau.mynotes.presentation.notes.drawer.DrawerAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

//https://developer.android.com/training/data-storage/shared/documents-files#create-file
enum class FilePickerMode {
    CreateFile,
    OpenFile,
}

@Composable
fun FilePicker(
    show: Boolean,
    mode: FilePickerMode,
    onFileHandled: (FilePickerMode, Uri?) -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            onFileHandled(mode, result.data?.data)
        }

    val downloadDirPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
    val intent = Intent().apply {
        when (mode) {
            FilePickerMode.CreateFile -> {
                action = Intent.ACTION_CREATE_DOCUMENT
                putExtra(Intent.EXTRA_TITLE, "mynotes_backup.json")
            }

            FilePickerMode.OpenFile -> {
                action = Intent.ACTION_OPEN_DOCUMENT
            }
        }
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/json"

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker before your app creates the document.
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(downloadDirPath))
    }
    LaunchedEffect(show) {
        if (show) {
            launcher.launch(intent)
        }
    }
}


fun isExternalStorageReadOnly(): Boolean {
    return Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()
}

fun isExternalStorageAvailable(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}

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

    LaunchedEffect(key1 = viewModel.state.insertedNoteId) {
        viewModel.state.insertedNoteId?.let {
            scope.launch {
                openNote(it)
            }
        }
    }

    val notesState =  viewModel.allNotes.collectAsState(initial = emptyList())

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
                    items.forEach {
                        if ((it is DrawerAction.Export && notesState.value.isNotEmpty() && isExternalStorageAvailable() && !isExternalStorageReadOnly()) ||
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