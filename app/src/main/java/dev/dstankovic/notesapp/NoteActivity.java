package dev.dstankovic.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dev.dstankovic.notesapp.models.Note;
import dev.dstankovic.notesapp.persistence.NoteRepository;
import dev.dstankovic.notesapp.util.LinedEditText;
import dev.dstankovic.notesapp.util.Utility;

public class NoteActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        TextWatcher {

    // constants
    private static final String TAG = NoteActivity.class.getSimpleName();
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    // ui components
    private LinedEditText mLinedEditText;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mBackArrowContainer;
    private RelativeLayout mCheckMarkContainer;
    private ImageButton mToolbarBackArrow;
    private ImageButton mToolbarCheckMark;

    // vars
    private boolean mIsNewNote;
    private Note mInitialNote;
    private Note mFinalNote;
    private GestureDetector mGestureDetector;
    private int mMode;
    private NoteRepository mNoteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mLinedEditText = findViewById(R.id.note_content);
        mEditTitle = findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_text_title);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mCheckMarkContainer = findViewById(R.id.check_mark_container);
        mToolbarBackArrow = findViewById(R.id.toolbar_back_arrow);
        mToolbarCheckMark = findViewById(R.id.toolbar_check_mark);
        mNoteRepository = new NoteRepository(this);

        setListeners();

        if (getIncomingIntent()) {
            // new note, edit mode
            setNewNoteProperties();
            enableEditMode();
        } else {
            // existing note, view mode
            setExistingNoteProperties();
            disableEditMode();
            disableContentInteraction();
        }

    }

    private void enableEditMode() {
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckMarkContainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;

        enableContentInteraction();
    }

    private void disableEditMode() {
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckMarkContainer.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;

        disableContentInteraction();

        String tempString = mLinedEditText.getText().toString();
        tempString = tempString.replace("\n", "");
        tempString = tempString.replace(" ", "");
        if (tempString.length() > 0) {
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLinedEditText.getText().toString());
            String timestamp = Utility.getCurrentTimestamp();
            mFinalNote.setTimestamp(timestamp);

            if (!mFinalNote.getContent().equals(mInitialNote.getContent()) ||
                    !mFinalNote.getTitle().equals(mInitialNote.getTitle())) {
                saveChanges();
            }
        }
    }

    private boolean getIncomingIntent() {
        if (getIntent().hasExtra("note")) {
            mInitialNote =  getIntent().getParcelableExtra("note");

            mFinalNote = new Note();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimestamp(mInitialNote.getTimestamp());
            mFinalNote.setId(mInitialNote.getId());

            mMode = EDIT_MODE_DISABLED;
            mIsNewNote = false;

            return false;
        } else {
            mMode = EDIT_MODE_ENABLED;
            mIsNewNote = true;

            return true;
        }
    }

    private void setListeners() {
        mLinedEditText.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this, this);
        mViewTitle.setOnClickListener(this);
        mToolbarCheckMark.setOnClickListener(this);
        mToolbarBackArrow.setOnClickListener(this);
        mEditTitle.addTextChangedListener(this);
    }

    private void setNewNoteProperties() {
        mViewTitle.setText("Note Title");
        mEditTitle.setText("Note Title");

        mInitialNote = new Note();
        mFinalNote = new Note();
        mInitialNote.setTitle("Note Title");
        mFinalNote.setTitle("Note Title");
    }

    private void setExistingNoteProperties() {
        mViewTitle.setText(mInitialNote.getTitle());
        mEditTitle.setText(mInitialNote.getTitle());
        mLinedEditText.setText(mInitialNote.getContent());
    }

    private void disableContentInteraction() {
        // prevent single click for entering note edit mode
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();
    }

    private void enableContentInteraction() {
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);
        mLinedEditText.requestFocus();
    }

    private void hideSoftKeyboard() {
        InputMethodManager manager =
                (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0); // hide soft keyboard
    }

    private void saveChanges() {
        if (mIsNewNote) {
            saveNewNote();
        } else {
            updateNote();
        }
    }

    private void updateNote() {
        mNoteRepository.updateNoteTask(mFinalNote);
    }

    private void saveNewNote() {
        mNoteRepository.insertNoteTask(mFinalNote);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: double tapped!");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_check_mark:
                hideSoftKeyboard();
                disableEditMode();
                break;
            case R.id.note_text_title:
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            case R.id.toolbar_back_arrow:
                finish(); // destroy activity
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // intercept back presses
        if (mMode == EDIT_MODE_ENABLED) {
            onClick(mToolbarCheckMark); // register click on the check mark
        } else {
            super.onBackPressed(); // standard behavior
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // preserve mode info on config change (rotation)
        outState.putInt("mode", mMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMode = savedInstanceState.getInt("mode");
        if (mMode == EDIT_MODE_ENABLED) {
            enableEditMode();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mViewTitle.setText(s.toString()); // when EditText text changes, set view title to new text
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}