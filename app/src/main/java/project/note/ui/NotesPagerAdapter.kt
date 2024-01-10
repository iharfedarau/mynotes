package project.note.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import project.note.database.Note

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
        NoteFragment(notes[position])

    fun updateItems(items: List<Note>) {
        notes = items
        notifyDataSetChanged()
    }

    private fun hash(note: Note) = note.hashCode().toLong()
}