package dev.dstankovic.notesapp.async;

import android.os.AsyncTask;

import dev.dstankovic.notesapp.models.Note;
import dev.dstankovic.notesapp.persistence.NoteDAO;

public class DeleteAsyncTask extends AsyncTask<Note, Void, Void> {

    private NoteDAO dao;

    public DeleteAsyncTask(NoteDAO dao) {
        this.dao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        dao.delete(notes);
        return null;
    }

}
