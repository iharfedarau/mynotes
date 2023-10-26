package project.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import project.note.NoteApplication
import project.note.data.Note
import project.note.data.NoteViewModel
import project.note.data.NoteViewModelFactory
import project.note.databinding.NotesFragmentLayoutBinding

class NotesFragment : Fragment() {
    private lateinit var binding: NotesFragmentLayoutBinding
    private lateinit var adapter: ScreenSlidePagerAdapter

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((activity?.application as NoteApplication).repository)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NotesFragmentLayoutBinding.inflate(inflater, container, false)

        adapter = ScreenSlidePagerAdapter(emptyList(), this)
        binding.pager.adapter = adapter

        noteViewModel.allNotes.observe(viewLifecycleOwner) { list ->
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

        childFragmentManager.setFragmentResultListener("deleteNoteRequestKey", viewLifecycleOwner) { _, bundle ->
            noteViewModel.delete(bundle.getInt("bundleDeleteNoteKey"))
        }

        childFragmentManager.setFragmentResultListener("saveNoteRequestKey", viewLifecycleOwner) { _, bundle ->
            bundle.getString("bundleSaveNoteKey")?.let {
                val note = Json.decodeFromString<Note>(it)
                noteViewModel.update(note)
            }
        }

        return binding.root
    }

    private inner class ScreenSlidePagerAdapter(private var notes: List<Note>, fa: Fragment) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = notes.size

        override fun createFragment(position: Int): Fragment =
            NoteFragment(notes[position])

        fun updateItems(items: List<Note>) {
            notes = items
            notifyDataSetChanged()
        }
    }
}