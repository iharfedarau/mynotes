package project.note.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            DrawerContent()
        },
        floatingActionButton = {
            FloatingActionButton {
                viewModel.insert(Note("Unknown", "")) {
                    onItemClick(it)
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
                .padding(16.dp, 16.dp, 16.dp, paddings.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(items = notes,
                itemContent = { _, item ->
                    Text(text = item.title,
                        modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                            .clickable {
                                onItemClick(item)
                            })
                    Divider(color = Color.Black, thickness = 1.dp)
                })
        }
    }
}