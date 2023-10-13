package project.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NotesFragmentLayoutBinding.inflate(inflater, container, false)

        adapter = ScreenSlidePagerAdapter(emptyList(), this)
        binding.pager.adapter = adapter

        noteViewModel.allNotes.observe(viewLifecycleOwner, Observer { list ->
            adapter = ScreenSlidePagerAdapter(list, this)
            binding.pager.adapter = adapter
        })

        binding.insert.setOnClickListener {
            noteViewModel.insert(Note("", ""))
        }

        binding.delete.setOnClickListener {
            if (adapter.notes.size > 0) {
                noteViewModel.delete(adapter.notes[binding.pager.currentItem].id)
            }
        }

        childFragmentManager.setFragmentResultListener("saveNoteRequestKey", viewLifecycleOwner) { requestKey, bundle ->
            bundle.getString("bundleSaveNoteKey")?.let {
                val note = Json.decodeFromString<Note>(it)
                noteViewModel.update(note)
            }
        }

        return binding.root
    }

    private inner class ScreenSlidePagerAdapter(val notes: List<Note>, fa: Fragment) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = notes.size

        override fun createFragment(position: Int): Fragment =
            NoteFragment(notes[position])
    }
}