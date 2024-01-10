package project.note.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class BottomNavigationBarItem(val route: String, val icon: ImageVector, val label: String) {
    data object Notes : BottomNavigationBarItem("notes", Icons.Default.List, "Notes")
    data object Reminders :  BottomNavigationBarItem("reminders", Icons.Default.Notifications, "Reminders")
}

@Composable
fun BottomNavigationBar(activity: MainActivity) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                listOf(
                    BottomNavigationBarItem.Notes,
                    BottomNavigationBarItem.Reminders
                ).forEach { item ->
                    BottomNavigationItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) }
                    )
                }
            }
        },
        content = { paddings ->
            NavHost(
                navController,
                BottomNavigationBarItem.Notes.route,
                Modifier.padding(paddings)
            ) {
                composable(BottomNavigationBarItem.Notes.route) {
                    /* Notes Screen UI */
                    NotesView(activity)
                }
                composable(BottomNavigationBarItem.Reminders.route) {
                    Text(text = "Not implemented yet")
                    /* Reminders Screen UI */
                }
            }
        }
    )
}