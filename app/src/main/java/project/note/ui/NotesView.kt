package project.note.ui

import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import project.note.databinding.NotesLayoutBinding

@Composable
fun NotesView(activity: MainActivity) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val view = NotesLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            activity.initializeNotesView(view)
            view.root
        },
        update = {
        }
    )
}