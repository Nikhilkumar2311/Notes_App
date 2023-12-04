package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.CreateActivity
import com.example.notesapp.Firebasemodel
import com.example.notesapp.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var mCreateNotesFab: FloatingActionButton
    private lateinit var mrecyclerview: RecyclerView
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportActionBar?.title = "All Notes"

        mCreateNotesFab = findViewById(R.id.create)

        firestore = FirebaseFirestore.getInstance()

        mCreateNotesFab.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
        mrecyclerview = findViewById(R.id.recyclerview)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mrecyclerview.layoutManager = linearLayoutManager

        val query = firestore
            .collection("notes")
            .orderBy("title", Query.Direction.ASCENDING)


        val allNotes = FirestoreRecyclerOptions.Builder<Firebasemodel>()
            .setQuery(query, Firebasemodel::class.java)
            .build()

        noteAdapter = NoteAdapter(allNotes, this)

        mrecyclerview.adapter = noteAdapter


        mrecyclerview.setHasFixedSize(true)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mrecyclerview.layoutManager = staggeredGridLayoutManager
        mrecyclerview.adapter = noteAdapter
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }

}

class NoteAdapter(options: FirestoreRecyclerOptions<Firebasemodel>, private val context: Context) :
    FirestoreRecyclerAdapter<Firebasemodel, NoteAdapter.NoteViewHolder>(options) {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notetitle: TextView = itemView.findViewById(R.id.notetitle)
        val notecontent: TextView = itemView.findViewById(R.id.notecontent)
        val mnote: LinearLayout = itemView.findViewById(R.id.note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notes_layout, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: Firebasemodel) {
        holder.notetitle.text = model.title
        holder.notecontent.text = model.content
    }
}