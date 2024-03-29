package project.note.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import project.note.domain.Note

@Composable
fun NotesView(notes: List<Note>, onItemClick: (note: Note) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
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
}