package dev.dstankovic.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dev.dstankovic.notesapp.adapters.NotesRecyclerAdapter;
import dev.dstankovic.notesapp.adapters.OnNoteListener;
import dev.dstankovic.notesapp.models.Note;
import dev.dstankovic.notesapp.persistence.NoteRepository;
import dev.dstankovic.notesapp.util.VerticalSpacingItemDecorator;

public class NotesListActivity extends AppCompatActivity implements
        OnNoteListener,
        View.OnClickListener {

    // constants
    private static final String TAG = NotesListActivity.class.getSimpleName();

    // ui components
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;

    // vars
    private List<Note> mNotes = new ArrayList<>();
    private NotesRecyclerAdapter mNotesRecyclerAdapter;
    private NoteRepository mNoteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        mRecyclerView = findViewById(R.id.notes_list);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mNoteRepository = new NoteRepository(this);

        initRecyclerView();

//        insertFakeNotes();
        retrieveNotes();

        setSupportActionBar((MaterialToolbar) findViewById(R.id.notes_list_toolbar));
        setTitle("Notes");
    }

    private void initRecyclerView() {
        mNotesRecyclerAdapter = new NotesRecyclerAdapter(mNotes, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        // attach itemtouchhelper to the recyclerview
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mNotesRecyclerAdapter);
    }

    private void insertFakeNotes() {
        for (int i = 0; i < 1000; i++) {
            Note note = new Note();
            note.setTitle("Title " + i);
            note.setContent("Content " + i);
            note.setTimestamp("Jan 2020");
            mNotes.add(note);
        }
        mNotesRecyclerAdapter.notifyDataSetChanged();
    }

    private void retrieveNotes() {
        mNoteRepository.retrieveNotesTask().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (mNotes.size() > 0) {
                    mNotes.clear();
                }
                if (notes != null) {
                    mNotes.addAll(notes);
                }
                mNotesRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note", mNotes.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        // fab onClick
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    private void deleteNote(Note note) {
        mNotes.remove(note);
        mNotesRecyclerAdapter.notifyDataSetChanged();

        mNoteRepository.deleteNoteTask(note);
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    deleteNote(mNotes.get(viewHolder.getAdapterPosition()));
                }
            };
}