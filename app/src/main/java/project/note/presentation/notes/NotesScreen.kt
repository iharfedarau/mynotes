package project.note.presentation.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import project.note.domain.repository.Note
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import project.note.BuildConfig
import project.note.R
import java.util.Calendar

@Composable
fun NotesScreen(onItemClick: (note: Note) -> Unit,
                viewModel: NotesViewModel = hiltViewModel()) {

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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddings)
                ) {
                    itemsIndexed(items = notes,
                        itemContent = { _, item ->
                            val delete = SwipeAction(
                                onSwipe = {
                                    viewModel.delete(item)
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.padding(16.dp),
                                        tint = Color.White
                                    )
                                }, background = Color.Red.copy(alpha = 0.5f),
                                isUndo = true
                            )

                            SwipeableActionsBox(
                                modifier = Modifier,
                                swipeThreshold = 200.dp,
                                endActions = listOf(delete)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().height(64.dp).clickable{
                                    onItemClick(item)
                                }) {
                                    Text(text = item.title,
                                        modifier = Modifier
                                            .weight(1.0f)
                                            .padding(16.dp))

                                    if (item.alarmDate != null) {
                                        Icon(
                                            imageVector = Icons.Filled.DateRange,
                                            contentDescription = null,
                                            modifier = Modifier.align(Alignment.CenterVertically).padding(16.dp),
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(thickness = 1.dp, color = Color.Black)
                        })
                }
            }
        }
    )
}