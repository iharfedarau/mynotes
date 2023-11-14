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
import androidx.fragment.app.FragmentActivity
import project.note.R

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
            val view = LayoutInflater.from(context).inflate(R.layout.main_activity_layout, null, false)

            // do whatever you want...
            view // return the view
        },
        update = {
        }
    )
}