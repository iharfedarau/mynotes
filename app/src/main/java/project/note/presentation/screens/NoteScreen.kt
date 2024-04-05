package project.note.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import project.note.R
import project.note.presentation.models.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(onBackClick: () -> Unit, viewModel: NoteViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.save()
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                title = {

                },
                actions = {
                    IconButton(onClick = {
                        viewModel.undo()
                    }, enabled = viewModel.undoRedo.canUndo) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.undo),
                            contentDescription = null,
                        )
                    }

                    IconButton(onClick = {
                        viewModel.redo()
                    }, enabled = viewModel.undoRedo.canRedo) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.redo),
                            contentDescription = null,
                        )
                    }

                    IconButton(onClick = {
                        coroutineScope.launch {
                            showBottomSheet = true
                        }

                    }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        content = { it ->
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {

                val title = viewModel.title
                TextField(
                    title,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    onValueChange = {
                        if (it.length <= 30) {
                            viewModel.updateTitle(it)
                        }
                    })

                TextField(viewModel.content, modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1.0f)
                    .verticalScroll(rememberScrollState()), onValueChange = {
                    viewModel.updateContent(it)
                })
            }

            if (showBottomSheet) {
                CustomBottomSheet(onDeleteAction = {
                    showBottomSheet = false
                    onBackClick()
                    viewModel.delete()
                }, onDismiss = {
                    showBottomSheet = false
                })
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(onDeleteAction: () -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(
        modifier = Modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        onDismissRequest = {
            onDismiss()
        },
        shape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        ),
    ) {
        CustomBottomSheetContainer(onDeleteAction)
    }
}

@Composable
fun CustomBottomSheetContainer(onDeleteAction: () -> Unit) {
    Scaffold(topBar = {
        Column {
            Text(
                text = "Note actions", modifier = Modifier
                    .height(75.dp)
                    .padding(start = 29.dp, top = 26.dp), fontSize = 23.sp
            )
            HorizontalDivider(thickness = 1.dp, color = Color.Black)
        }
    }) {
        Column(modifier = Modifier.padding(it)) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable {
                    onDeleteAction()
                }, contentAlignment = Alignment.CenterStart) {
                Text(
                    text = "Delete",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}