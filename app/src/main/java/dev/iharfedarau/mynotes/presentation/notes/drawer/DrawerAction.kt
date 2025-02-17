package dev.iharfedarau.mynotes.presentation.notes.drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.vector.ImageVector
import dev.iharfedarau.mynotes.BuildConfig
import dev.iharfedarau.mynotes.R

sealed class DrawerAction(val icon: ImageVector, val textResId: Int, val onClick: () -> Unit, val extraText: String? = null) {
    class Export(onClick: () -> Unit): DrawerAction(Icons.Default.KeyboardArrowUp, R.string.export_data, onClick)
    class Import(onClick: () -> Unit): DrawerAction(Icons.Default.KeyboardArrowDown, R.string.import_data, onClick)
    class About(onClick: () -> Unit): DrawerAction(Icons.Default.Info, R.string.version, onClick, BuildConfig.VERSION_NAME)
}