import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.MainActivity
import com.example.noteapp.Note
import com.example.noteapp.NotesDatabaseHelper
import com.example.noteapp.R
import com.example.noteapp.UpdateNoteActivity

class NotesAdapter(private var notes: List<Note>, private val context: Context) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val db: NotesDatabaseHelper = NotesDatabaseHelper(context)
    private var originalNotesList: List<Note> = notes.toList()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val pinButton: ImageView = itemView.findViewById(R.id.pinButton)
    }

    fun performSearch(keyword: String) {
        notes = if (keyword.isEmpty()) {
            originalNotesList
        } else {
            db.searchNotes(keyword)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        // Set pinned/unpinned icon based on note's pinned state
        if (note.isPinned) {
            holder.pinButton.setImageResource(R.drawable.baseline_remove_circle_outline_24)
        } else {
            holder.pinButton.setImageResource(R.drawable.baseline_push_pin_24)
        }

        holder.pinButton.setOnClickListener {
            // Toggle pinned state
            note.isPinned = !note.isPinned

            // Update pinned/unpinned icon
            if (note.isPinned) {
                holder.pinButton.setImageResource(R.drawable.baseline_push_pin_24 )
                Toast.makeText(context, "Note pinned", Toast.LENGTH_SHORT).show()
                // Move the pinned note to the top
                val pinnedNoteIndex = notes.indexOf(note)
                if (pinnedNoteIndex != -1) {
                    notes = listOf(note) + notes.filterIndexed { index, _ -> index != pinnedNoteIndex }
                    notifyDataSetChanged()
                }
            } else {
                holder.pinButton.setImageResource(R.drawable.baseline_remove_circle_outline_24)
                Toast.makeText(context, "Note unpinned", Toast.LENGTH_SHORT).show()
            }
        }


        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateNoteActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(note)
        }
    }

    private fun showDeleteConfirmationDialog(note: Note) {
        AlertDialog.Builder(context)
            .setMessage("Do you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                (context as MainActivity).deleteNote(note.id) // Call deleteNote from MainActivity
                Toast.makeText(context, "Your Note Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    fun refreshData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}