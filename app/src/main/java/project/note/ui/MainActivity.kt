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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import project.note.NoteApplication.Companion.dataStore
import project.note.database.Note
import project.note.databinding.NotesLayoutBinding
import project.note.viewmodels.NoteViewModel

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private var keepSplashScreen = true
    private lateinit var adapter: NotesPagerAdapter
    private lateinit var pager: ViewPager2
    private val noteViewModel: NoteViewModel by viewModels()
    private var pagerCurrentItem = -1

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

        lifecycleScope.launch {
            dataStore.data.first { preferences ->
                pagerCurrentItem = preferences[PREFERENCE_KYE_PAGER_CURRENT_ITEM] ?: -1

                noteViewModel.allNotes.observe(this@MainActivity) { list ->
                    keepSplashScreen = false

                    if (adapter.itemCount != list.size) {
                        adapter.updateItems(list)

                        if (pagerCurrentItem != -1 && adapter.itemCount > pagerCurrentItem) {
                            binding.pager.currentItem = pagerCurrentItem
                            pagerCurrentItem = -1
                        } else{
                            binding.pager.currentItem = list.size - 1
                        }
                    }
                }

                true
            }
        }

        supportFragmentManager.setFragmentResultListener(
            "NoteFragment",
            this
        ) { _, bundle ->
            bundle.getSerializable("Save")?.let {
                noteViewModel.update(it as Note)
            }
        }

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                lifecycleScope.launch {
                    this@MainActivity.dataStore.edit { preferences ->
                        preferences[PREFERENCE_KYE_PAGER_CURRENT_ITEM] = position
                    }
                }
            }
        })
    }

    fun addNote() {
        noteViewModel.insert(Note("", ""))
    }

    fun removeNote() {
        if (adapter.itemCount > 0) {
            noteViewModel.delete(adapter.note(pager.currentItem).id)
        }
    }

    companion object {
        private val PREFERENCE_KYE_PAGER_CURRENT_ITEM = intPreferencesKey("PAGER_CURRENT_ITEM")
    }
}