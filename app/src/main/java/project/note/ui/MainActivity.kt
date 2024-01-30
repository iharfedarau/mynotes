package project.note.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface

import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import project.note.database.Note
import project.note.databinding.NotesLayoutBinding
import project.note.viewmodels.NoteViewModel

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private var keepSplashScreen = true
    private lateinit var adapter: NotesPagerAdapter
    private lateinit var pager: ViewPager2
    private val noteViewModel: NoteViewModel by viewModels()

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
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BottomNavigationBar(this)
                }
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun initializeNotesView(binding: NotesLayoutBinding) {
        adapter = NotesPagerAdapter(emptyList(), this)
        pager = binding.pager
        binding.pager.adapter = adapter

        noteViewModel.allNotes.observe(this) { list ->
            keepSplashScreen = false

            if (adapter.itemCount != list.size) {
                adapter.updateItems(list)
                binding.pager.currentItem = list.size - 1
            }
        }

        supportFragmentManager.setFragmentResultListener(
            "saveNoteRequestKey",
            this
        ) { _, bundle ->
            bundle.getString("bundleSaveNoteKey")?.let {
                val note = Json.decodeFromString(Note.serializer(), it)
                noteViewModel.update(note)
            }
        }
    }

    fun addNote() {
        noteViewModel.insert(Note("", ""))
    }

    fun removeNote() {
        if (adapter.itemCount > 0) {
            noteViewModel.delete(adapter.note(pager.currentItem).id)
        }
    }
}