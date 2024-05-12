package com.example.noteapp

import NotesAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        notesAdapter = NotesAdapter(db.getAllNotes(), this)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                notesAdapter.performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Load initial notes
        loadNotesFromDatabase()
    }

    private fun loadNotesFromDatabase() {
        val notes = db.getAllNotes()
        notesAdapter.refreshData(notes)

        val notesCountText = getString(R.string.notes_count_placeholder, notes.size)
        binding.notesCountTextView.text = notesCountText
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(db.getAllNotes())
        loadNotesFromDatabase()
    }

    // Function to handle note deletion
    fun deleteNote(noteId: Int) {
        db.deleteNote(noteId)
        val notes = db.getAllNotes()
        notesAdapter.refreshData(notes)

        // Update note count after deletion
        val notesCountText = getString(R.string.notes_count_placeholder, notes.size)
        binding.notesCountTextView.text = notesCountText
    }

}