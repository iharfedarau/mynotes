package project.note.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import project.note.database.Note

class PagerDiffUtil(private val oldList: List<Note>, private val newList: List<Note>) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

class NotesPagerAdapter(private var notes: List<Note>, fa: FragmentActivity) :
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
        NoteFragment().apply {
            setNote(notes[position])
        }

    fun updateItems(newNotes: List<Note>) {
        val callback = PagerDiffUtil(notes, newNotes)
        val diff = DiffUtil.calculateDiff(callback)
        notes = newNotes
        diff.dispatchUpdatesTo(this)
    }

    fun note(position: Int): Note {
        return notes[position]
    }

    private fun hash(note: Note) = note.hashCode().toLong()
}