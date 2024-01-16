package project.note.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.serialization.json.Json
import project.note.database.Note
import project.note.databinding.NoteFragmentLayoutBinding

class NoteFragment : Fragment() {
    private lateinit var note: Note
    private lateinit var binding: NoteFragmentLayoutBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getString(NOTE_SERIALIZATION_KEY)?.let {
            note = Json.decodeFromString(Note.serializer(), it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NoteFragmentLayoutBinding.inflate(inflater, container, false)
        binding.title.setText(note.title)
        binding.content.setText(note.content)


        return binding.root
    }

    fun modifiedNote() =
        Note(binding.title.text.toString(), binding.content.text.toString(), note.id)

    companion object {
        const val NOTE_SERIALIZATION_KEY = "NOTE_SERIALIZATION_KEY"
    }
}