package project.note.presentation.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import project.note.R
import project.note.presentation.alarm.AlarmItem
import project.note.presentation.utils.toFormattedDateTime
import project.note.presentation.utils.toLocalDate
import project.note.presentation.utils.toLong
import java.time.LocalDateTime
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(onBackClick: () -> Unit, viewModel: NoteViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable {  mutableStateOf(false) }

    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var datePickerState: DatePickerState? = null
    var timePickerState: TimePickerState? = null

    if (viewModel.note != null) {
        val ldt = viewModel.alarmItem?.date
        datePickerState = rememberDatePickerState(initialSelectedDateMillis = ldt?.toLong())
        timePickerState = rememberTimePickerState(initialHour = ldt?.hour?: 0, initialMinute = ldt?.minute ?: 0)
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
                    }, enabled = viewModel.note != null) {
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
                
                if (viewModel.alarmItem != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {

                        }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = null,
                            )
                        }
                        Text(text = viewModel.alarmItem?.date?.toLong()?.toFormattedDateTime() ?: "")

                        IconButton(onClick = {
                            viewModel.updateAlarm(null)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                            )
                        }
                    }
                }
                
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
                CustomBottomSheet {
                    showBottomSheet = false

                    when (it) {
                        CustomBottomSheetAction.Delete -> {
                            onBackClick()
                            viewModel.delete()
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
                    DatePicker(state = datePickerState!!)
                }
            }

            if (showTimePicker) {
                TimePickerDialog(
                    content = {
                        TimePicker(state = timePickerState!!)
                    },
                    onCancel = {
                        showTimePicker = false
                    },
                    onConfirm = {
                        showTimePicker = false

                        datePickerState?.selectedDateMillis?.let {date ->
                            timePickerState?.let { time ->
                                val ldt = LocalDateTime.of(date.toLocalDate(), LocalTime.of(time.hour, time.minute))
                                viewModel.updateAlarm(AlarmItem(ldt, viewModel.title))
                            }
                        }
                    })
            }
        }
    )
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
                text = "Note actions", modifier = Modifier
                    .height(48.dp), fontSize = 23.sp
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