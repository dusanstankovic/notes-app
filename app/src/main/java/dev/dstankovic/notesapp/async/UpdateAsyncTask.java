package dev.dstankovic.notesapp.async;

import android.os.AsyncTask;

import dev.dstankovic.notesapp.models.Note;
import dev.dstankovic.notesapp.persistence.NoteDAO;

public class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {

    private NoteDAO dao;

    public UpdateAsyncTask(NoteDAO dao) {
        this.dao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        dao.update(notes);
        return null;
    }

}
