package project.note.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import project.note.database.Note
import project.note.databinding.MainActivityLayoutBinding
import project.note.viewmodels.NoteViewModel

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private var keepSplashScreen = true
    private lateinit var binding: MainActivityLayoutBinding
    private lateinit var adapter: ScreenSlidePagerAdapter
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
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content(this)
                }
            }
        }
    }

    fun initialize(context: Context): View {
        binding = MainActivityLayoutBinding.inflate(LayoutInflater.from(context), null, false)

        adapter = ScreenSlidePagerAdapter(emptyList(), this)
        binding.pager.adapter = adapter

        noteViewModel.allNotes.observe(this) { list ->
            keepSplashScreen = false

            // Insertion
            if (adapter.itemCount > 0 && adapter.itemCount < list.size) {
                adapter.updateItems(list)
                binding.pager.currentItem = list.size - 1
            } else {
                adapter.updateItems(list)
            }
        }

        binding.insert.setOnClickListener {
            noteViewModel.insert(Note("", ""))
        }

        supportFragmentManager.setFragmentResultListener(
            "deleteNoteRequestKey",
            this
        ) { _, bundle ->
            noteViewModel.delete(bundle.getInt("bundleDeleteNoteKey"))
        }

        supportFragmentManager.setFragmentResultListener(
            "saveNoteRequestKey",
            this
        ) { _, bundle ->
            bundle.getString("bundleSaveNoteKey")?.let {
                val note = Json.decodeFromString<Note>(it)
                noteViewModel.update(note)
            }
        }

        return binding.root
    }

    inner class ScreenSlidePagerAdapter(private var notes: List<Note>, fa: FragmentActivity) :
        FragmentStateAdapter(fa) {
        override fun getItemId(position: Int): Long {
            return hash(notes[position])
        }

        override fun containsItem(itemId: Long): Boolean {
            return notes.any { note ->
                itemId == hash(note)
            }
        }

        override fun getItemCount(): Int = notes.size

        override fun createFragment(position: Int): Fragment =
            NoteFragment(notes[position])

        fun updateItems(items: List<Note>) {
            notes = items
            notifyDataSetChanged()
        }

        private fun hash(note: Note) = note.hashCode().toLong()
    }
}

@Composable
fun Content(activity: MainActivity) {
    AndroidView(
        factory = { context ->
            activity.initialize(context)
        },
        update = {
        }
    )
}