package dev.dstankovic.notesapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.dstankovic.notesapp.R;
import dev.dstankovic.notesapp.models.Note;
import dev.dstankovic.notesapp.util.Utility;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    public static final String TAG = NotesRecyclerAdapter.class.getSimpleName();
    private List<Note> mNotes;
    private OnNoteListener onNoteListener;

    public NotesRecyclerAdapter(List<Note> notes,OnNoteListener onNoteListener) {
        mNotes = notes;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent).getContext())
                .inflate(R.layout.layout_note_list_item, parent, false);
        return new NoteViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        try {
            String month = mNotes.get(position).getTimestamp().substring(0, 2);
            month = Utility.getMonthFromNumber(month);
            String year = mNotes.get(position).getTimestamp().substring(3);
            String timestamp = month + " " + year;
            holder.noteTitle.setText(mNotes.get(position).getTitle());
            holder.noteTimestamp.setText(timestamp);
        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder: error - " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if (mNotes != null) {
            return mNotes.size();
        }
        return 0;
    }
}
