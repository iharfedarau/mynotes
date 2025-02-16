package dev.iharfedarau.mynotes.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.iharfedarau.mynotes.presentation.editnote.RootEditNoteScreen
import dev.iharfedarau.mynotes.presentation.notes.NotesScreen
import dev.iharfedarau.mynotes.presentation.ui.NoteAppTheme
import dev.iharfedarau.mynotes.presentation.utils.EditNoteScreenRoute
import dev.iharfedarau.mynotes.presentation.utils.NotesScreenRoute

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
            NoteAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NotesScreenRoute
                ) {
                    composable<NotesScreenRoute> {
                        NotesScreen(openNote = { noteId ->
                            navController.navigate(EditNoteScreenRoute(noteId))
                        })
                    }

                    composable<EditNoteScreenRoute> {
                        RootEditNoteScreen(navController)
                    }
                }
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
