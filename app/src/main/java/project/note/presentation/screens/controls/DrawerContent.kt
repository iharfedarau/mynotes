package project.note.presentation.screens.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import project.note.BuildConfig
import project.note.R

@Composable
fun DrawerContent() {
    Box (modifier = Modifier.fillMaxSize().background(Color(203, 223, 248, 255))){
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.version) + ": " + BuildConfig.VERSION_NAME)
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }

}