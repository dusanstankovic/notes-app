package dev.dstankovic.notesapp.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.dstankovic.notesapp.R;

public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView noteTitle;
    TextView noteTimestamp;
    OnNoteListener onNoteListener;

    public NoteViewHolder(@NonNull View itemView,OnNoteListener onNoteListener) {
        super(itemView);
        noteTitle = itemView.findViewById(R.id.note_title);
        noteTimestamp = itemView.findViewById(R.id.note_timestamp);

        this.onNoteListener = onNoteListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onNoteListener.onNoteClick(getAdapterPosition());
    }
}
