package project.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import kotlinx.serialization.json.Json
import project.note.database.Note
import project.note.databinding.NoteFragmentLayoutBinding

class NoteFragment : Fragment() {
    private lateinit var note: Note
    private lateinit var binding: NoteFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NoteFragmentLayoutBinding.inflate(inflater, container, false)
        binding.title.apply {
            setText(note.title)
            doAfterTextChanged {
                sendChangeResult()
            }
        }

        binding.content.apply {
            setText(note.content)
            doAfterTextChanged {
                sendChangeResult()
            }
        }

        return binding.root
    }

    fun setNote(note: Note) {
        this.note = note
    }

    private fun sendChangeResult() {
        setFragmentResult(
            "saveNoteRequestKey",
            bundleOf(
                "bundleSaveNoteKey" to Json.encodeToString(
                    Note.serializer(),
                    Note(
                        binding.title.text.toString(),
                        binding.content.text.toString(),
                        note.id
                    )
                )
            )
        )
    }
}