package project.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.note.R

class MainActivity : FragmentActivity() {
    private var keepSplashScreen = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
            .apply {
                setKeepOnScreenCondition {
                    keepSplashScreen
                }
            }

        lifecycleScope.launch {
            delay(2000)

            withContext(Dispatchers.Main) {
                keepSplashScreen = false
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }
}

@Composable
fun Content() {
    AndroidView(
        factory = { context ->
            val view =
                LayoutInflater.from(context).inflate(R.layout.main_activity_layout, null, false)

            // do whatever you want...
            view // return the view
        },
        update = {
        }
    )
}