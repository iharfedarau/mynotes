package project.note.presentation.editnote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import project.note.R
import project.note.domain.alarm.AlarmItem
import project.note.presentation.ui.NoteAppTheme
import project.note.presentation.utils.NotesScreenRoute
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RootEditNoteScreen(
    navController: NavHostController,
    viewModel: EditNoteViewModel = hiltViewModel()
) {
    EditNoteScreen(
        state = viewModel.state,
        uiAction = {
            when (it) {
                EditNoteAction.GoBack -> {
                    navController.navigate(NotesScreenRoute)
                }

                else -> {
                    viewModel.onUiAction(it)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    state: EditNoteState,
    uiAction: (EditNoteAction) -> Unit
) {
    if (state.editNote != null) {
        val coroutineScope = rememberCoroutineScope()
        var showBottomSheet by rememberSaveable { mutableStateOf(false) }

        var showTimePicker by remember { mutableStateOf(false) }
        var showDatePicker by remember { mutableStateOf(false) }

        val ldt = if (state.editNote.alarmItem?.date != null) {
            OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(state.editNote.alarmItem.date),
                ZoneId.systemDefault()
            )
        } else {
            OffsetDateTime.now()
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = ldt.toEpochSecond() * 1000
        )
        val timePickerState =
            rememberTimePickerState(initialHour = ldt.hour, initialMinute = ldt.minute)

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            uiAction(EditNoteAction.SaveAction)
                            uiAction(EditNoteAction.GoBack)
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
                            uiAction(EditNoteAction.UndoAction)
                        }, enabled = state.canUndo) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.undo),
                                contentDescription = null,
                            )
                        }

                        IconButton(onClick = {
                            uiAction(EditNoteAction.RedoAction)
                        }, enabled = state.canRedo) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.redo),
                                contentDescription = null,
                            )
                        }

                        IconButton(onClick = {
                            uiAction(EditNoteAction.SaveAction)
                        }, enabled = state.canSave) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                            )
                        }

                        IconButton(onClick = {
                            coroutineScope.launch {
                                showBottomSheet = true
                            }
                        }, enabled = true) {
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

                    if (state.editNote.alarmItem != null) {
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .background(Color.Black)
                                .fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(color = NoteAppTheme.colors.surfaceVariant),
                        ) {
                            IconButton(onClick = {
                                showDatePicker = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                )
                            }

                            Text(
                                text = LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(state.editNote.alarmItem.date),
                                    ZoneId.systemDefault()
                                ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),

                                modifier = Modifier
                                    .weight(1.0f)
                                    .align(Alignment.CenterVertically)
                            )

                            IconButton(onClick = {
                                uiAction(EditNoteAction.SetAlarmAction(null))
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color.Black, thickness = 1.dp)

                    TextField(
                        state.editNote.title,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = TextFieldDefaults.colors().copy(
                            unfocusedContainerColor = NoteAppTheme.colors.onPrimary,
                            focusedContainerColor = NoteAppTheme.colors.onPrimary,
                            errorIndicatorColor = NoteAppTheme.colors.onPrimary,
                            focusedIndicatorColor = NoteAppTheme.colors.onPrimary,
                            unfocusedIndicatorColor = NoteAppTheme.colors.onPrimary
                        ),
                        onValueChange = {
                            if (it.length <= 30) {
                                uiAction(EditNoteAction.SetTitleAction(it))
                            }
                        })
                    HorizontalDivider(color = Color.Black, thickness = 1.dp)

                    TextField(state.editNote.content, modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                        colors = TextFieldDefaults.colors().copy(
                            unfocusedContainerColor = NoteAppTheme.colors.onPrimary,
                            focusedContainerColor = NoteAppTheme.colors.onPrimary,
                            errorIndicatorColor = NoteAppTheme.colors.onPrimary,
                            focusedIndicatorColor = NoteAppTheme.colors.onPrimary,
                            unfocusedIndicatorColor = NoteAppTheme.colors.onPrimary
                        ), onValueChange = {
                            uiAction(EditNoteAction.SetContentAction(it))
                        })
                }

                if (showBottomSheet) {
                    CustomBottomSheet {
                        showBottomSheet = false

                        when (it) {
                            CustomBottomSheetAction.Delete -> {
                                uiAction(EditNoteAction.GoBack)
                                uiAction(EditNoteAction.DeleteAction)
                            }

                            CustomBottomSheetAction.Dismiss -> {
                                showBottomSheet = false
                            }

                            CustomBottomSheetAction.SetAlarm -> {
                                showDatePicker = true
                            }
                        }

                    }
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = {
                            showDatePicker = false
                        },
                        confirmButton = {
                            Button(onClick = {
                                showDatePicker = false
                                showTimePicker = true
                            }

                            ) {
                                Text(text = "OK")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                showDatePicker = false
                            }) {
                                Text(text = "Cancel")
                            }
                        }
                    )
                    {
                        DatePicker(state = datePickerState)
                    }
                }

                if (showTimePicker) {
                    TimePickerDialog(
                        content = {
                            TimePicker(state = timePickerState)
                        },
                        onCancel = {
                            showTimePicker = false
                        },
                        onConfirm = {
                            showTimePicker = false

                            datePickerState.selectedDateMillis?.let { dateMs ->
                                val ldtLocal = dateMs +
                                        timePickerState.hour * 3600 * 1000 +
                                        timePickerState.minute * 60 * 1000 -
                                        OffsetDateTime.now().offset.totalSeconds * 1000

                                uiAction(
                                    EditNoteAction.SetAlarmAction(
                                        AlarmItem(
                                            ldtLocal, state.editNote.title
                                        )
                                    )
                                )
                            }
                        })
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(action: (CustomBottomSheetAction) -> Unit) {
    ModalBottomSheet(
        modifier = Modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        onDismissRequest = {
            action(CustomBottomSheetAction.Dismiss)
        },
        shape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        ),
    ) {
        CustomBottomSheetContainer(action)
    }
}

@Composable
fun CustomBottomSheetContainer(action: (CustomBottomSheetAction) -> Unit) {
    Scaffold(topBar = {
        Column {
            Text(
                text = stringResource(id = R.string.note_actions), modifier = Modifier
                    .padding(16.dp), fontSize = 23.sp
            )
            HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
        }
    }) {
        Column(modifier = Modifier.padding(it)) {
            CustomBottomSheetItem(stringResource(id = R.string.set_alarm)) {
                action(CustomBottomSheetAction.SetAlarm)
            }

            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

            CustomBottomSheetItem(stringResource(id = R.string.delete)) {
                action(CustomBottomSheetAction.Delete)
            }
        }
    }
}