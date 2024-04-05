package project.note.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import project.note.presentation.utils.Routes
import project.note.presentation.screens.NoteScreen
import project.note.presentation.screens.NotesScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var keepSplashScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
            .apply {
                setKeepOnScreenCondition {
                    keepSplashScreen
                }
            }

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.NOTES_VIEW
                ) {
                    composable(Routes.NOTES_VIEW) {
                        NotesScreen(onItemClick = {note ->
                            navController.navigate(Routes.NOTE_VIEW + "/${note.id}")
                        })
                    }

                    composable(               
                        route = Routes.NOTE_VIEW + "/{noteId}",
                        arguments = listOf(
                            navArgument(name = "noteId") {
                                type = NavType.LongType
                                defaultValue = -1
                            }
                        )
                        ) {
                        NoteScreen(onBackClick = {
                            navController.navigate(Routes.NOTES_VIEW)
                        })
                    }
                }
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
