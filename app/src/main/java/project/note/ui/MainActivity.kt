package project.note.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Divider
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import project.note.database.Note
import project.note.viewmodels.NoteViewModel

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private var keepSplashScreen = true
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteViewModel.allNotes.observe(this) {
            keepSplashScreen = false
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
                var showNotes by remember { mutableStateOf(false) }
                var currentNote by remember { mutableStateOf<Note?>(null) }
                val notes by noteViewModel.allNotes.observeAsState(initial = emptyList())

                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerContent = {

                    },
                    floatingActionButton = {
                        if (!showNotes) {
                            ExtendedFloatingActionButton(
                                text = { Text("New") },
                                onClick = {
                                    noteViewModel.insert(Note("Unknown", ""))
                                    noteViewModel.returnedVal.observe(this@MainActivity) {
                                        currentNote = it
                                        showNotes = true
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        BottomAppBar {
                            IconButton(onClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
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
                ) {

                    val cn = currentNote
                    if (showNotes && cn != null) {
                        NotesView(cn, it, {
                            showNotes = false

                        },  { note ->
                            noteViewModel.update(note)
                        }, {id ->
                            noteViewModel.delete(id).invokeOnCompletion {
                                currentNote = null
                                showNotes = false
                            }
                        })
                    } else {


                        LazyColumn(modifier = Modifier.fillMaxWidth().padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            itemsIndexed(items = notes,
                                itemContent = {index, item ->
                                    ClickableText(modifier = Modifier.fillMaxWidth(),
                                        text = AnnotatedString(item.title) ,
                                        onClick = {
                                            currentNote = item
                                            showNotes = true
                                        })
                                        Divider(color = Color.Black, thickness = 1.dp)
                                })
                        }
                    }
                }
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        fun addNote() {
            noteViewModel.insert(Note("Unknown", ""))
        }
    }
}