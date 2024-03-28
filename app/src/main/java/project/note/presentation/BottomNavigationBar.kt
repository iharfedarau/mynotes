package project.note.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import project.note.R

sealed class BottomNavigationBarItem(val route: String, val icon: ImageVector, val label: Int) {
    data object Notes : BottomNavigationBarItem("notes", Icons.Default.List, R.string.notes)
    data object Reminders :  BottomNavigationBarItem("reminders", Icons.Default.Notifications, R.string.reminders)
}