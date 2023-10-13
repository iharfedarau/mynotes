package project.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import project.note.data.Note
import project.note.databinding.NoteFragmentLayoutBinding

class NoteFragment(private val note: Note): Fragment() {
    private lateinit var binding: NoteFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NoteFragmentLayoutBinding.inflate(inflater, container, false)
        binding.title.setText(note.title)
        binding.content.setText(note.content)

        binding.save.setOnClickListener {
            setFragmentResult("saveNoteRequestKey",
                bundleOf("bundleSaveNoteKey" to Json.encodeToString(
                    Note(binding.title.text.toString(),  binding.content.text.toString(), note.id))))
        }

        return binding.root
    }
}