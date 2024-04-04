package project.note.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import project.note.domain.Note
import project.note.presentation.model.NoteViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var keepSplashScreen = true
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            noteViewModel.allNotes.collectLatest {
                keepSplashScreen = false
            }
        }

        installSplashScreen()
            .apply {
                setKeepOnScreenCondition {
                    keepSplashScreen
                }
            }

        setContent {
            MaterialTheme {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                var uiState by rememberSaveable { mutableStateOf<UiState>(UiState.ShowNotesView) }
                val notes by noteViewModel.allNotes.collectAsState(initial = emptyList())

                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerContent = {
                        DrawerContent()
                    },
                    floatingActionButton = {
                        if (uiState is UiState.ShowNotesView) {
                            FloatingActionButton {
                                uiState = UiState.InProgress
                                noteViewModel.insert(Note("Unknown", "")) {
                                    uiState = UiState.ShowNoteView(it)
                                }
                            }
                        }
                    },
                    bottomBar = {
                        if (uiState !is UiState.InProgress) {
                            BottomBar(onMenuClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            })
                        }
                    }
                ) { paddings ->
                    when (uiState) {
                        UiState.InProgress -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.width(64.dp))
                            }
                        }

                        is UiState.ShowNoteView -> {
                            NoteView(
                                (uiState as UiState.ShowNoteView).currentNote,
                                paddings,
                                back = {
                                    uiState = UiState.ShowNotesView
                                },
                                save = { note ->
                                    noteViewModel.update(note)
                                },
                                delete = { id ->
                                    uiState = UiState.InProgress
                                    noteViewModel.delete(id).invokeOnCompletion {
                                        uiState = UiState.ShowNotesView
                                    }
                                })
                        }

                        UiState.ShowNotesView -> {
                            NotesView(notes, onItemClick = { note ->
                                uiState = UiState.ShowNoteView(note)
                            })
                        }
                    }
                }
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
