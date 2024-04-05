package project.note.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import project.note.domain.Note
import project.note.presentation.models.NotesViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import project.note.presentation.screens.controls.BottomBar
import project.note.presentation.screens.controls.DrawerContent
import project.note.presentation.screens.controls.FloatingActionButton

@Composable
fun NotesScreen(onItemClick: (note: Note) -> Unit,
              viewModel: NotesViewModel = hiltViewModel()) {
    val scaffoldState = rememberScaffoldState()
    var inProgress by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            DrawerContent()
        },
        floatingActionButton = {
            FloatingActionButton {
                inProgress = true
                viewModel.insert(Note("Unknown", "")) {
                    onItemClick(it)
                    inProgress = false
                }
            }
        },
        bottomBar = {
            val scope = rememberCoroutineScope()

            BottomBar(onMenuClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            })
        }
    ) { paddings ->
        val notes by viewModel.allNotes.collectAsState(initial = emptyList())

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddings)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(items = notes,
                itemContent = { _, item ->
                    ClickableText(modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                        text = AnnotatedString(item.title),
                        onClick = {
                            onItemClick(item)
                        })
                    Divider(color = Color.Black, thickness = 1.dp)
                })
        }

        if (inProgress) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.width(64.dp))
            }
        }
    }
}