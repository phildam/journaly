package com.squadron.philip.journaly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.squadron.philip.journaly.database.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteAdder extends AppCompatActivity {

    private EditText mContent;
    private Button mSubmit;
    private AppDatabase mAppDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_adder);

        mAppDatabase = AppDatabase.getInstance(getApplicationContext());

        componentLoader();

    }

    private void componentLoader(){
        mContent=(EditText)findViewById(R.id.noteAdderContent);
        mSubmit=(Button)findViewById(R.id.AddNewNoteButton);
    }

    private String getCurrentDateInfo(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        Log.d("philcorp","Calendardate "+df.format(calendar.getTime()));
        String theDate=df.format(calendar.getTime());
        return theDate;
    }

    public void onRequestSave() {
        String content = mContent.getText().toString();
        Date journalDate = new Date();

       // JournalEntity journalEntry =new JournalEntity(content, journalDate )
    }



}
