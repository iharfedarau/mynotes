package project.note.presentation.editnote

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomBottomSheetItem(text: String, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .clickable {
            onClick()
        }, contentAlignment = Alignment.CenterStart) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}