package project.note.presentation.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import project.note.domain.repository.Note

@Composable
fun NoteItem(note: Note, onClick: (Note) -> Unit, onDelete: (Note) -> Unit) {
    val delete = SwipeAction(
        onSwipe = {
            onDelete(note)
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
            onClick(note)
        }) {
            Text(text = note.title,
                modifier = Modifier
                    .weight(1.0f)
                    .padding(16.dp))

            if (note.alarmDate != null) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(16.dp),
                )
            }
        }
    }

    HorizontalDivider(thickness = 1.dp, color = Color.Black)
}
