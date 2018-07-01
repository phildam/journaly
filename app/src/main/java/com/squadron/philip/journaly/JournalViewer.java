package com.squadron.philip.journaly;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squadron.philip.journaly.database.AppDatabase;
import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class JournalViewer extends AppCompatActivity {

    private AppDatabase mAppDatabase;
    private ImageView imageView;
    private JournalEntity journalEntity;
    private AppExecutors executors;
    private TextView journaltext;
    private TextView journalLocation;
    private TextView journalLastUpDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_adder);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        journaltext = (TextView) findViewById(R.id.journal_viewer_text);
        journalLocation = (TextView) findViewById(R.id.journal_location);
        journalLastUpDateTime = (TextView) findViewById(R.id.journal_last_update);

        journalEntity = (JournalEntity)getIntent().getSerializableExtra(MainActivity.EDITOR);
        journaltext.setText(journalEntity.getContent());
        journalLastUpDateTime.setText((journalEntity.getLastModifiedDate().toGMTString()));
        journalLocation.setText(journalEntity.getLocation());

        executors = new AppExecutors();
        mAppDatabase = AppDatabase.getInstance(getApplicationContext());
        int randomImageSelector = Processor.imageSelector(
                new Random().nextInt((20 - 0) + 1)
        );
        imageView = (ImageView) findViewById(R.id.image_viewer);
        imageView.setImageResource(randomImageSelector);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal_viewer, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                 journalEntity =  mAppDatabase.journalDao().loadJournalEntityById(journalEntity.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            journaltext.setText(journalEntity.getContent());
                            journalLastUpDateTime.setText((journalEntity.getLastModifiedDate().toGMTString()));
                            journalLocation.setText(journalEntity.getLocation());
                        }
                    });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete_journal:
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mAppDatabase.journalDao().deleteJournal(journalEntity);
                        finish();
                    }
                });
                return true;
            case R.id.action_edit_journal:
                Intent intent = new Intent(this, JournalEditor.class);
                intent.putExtra(MainActivity.EDITOR, journalEntity);
                intent.putExtra(MainActivity.USERNAME, getIntent().getStringExtra(MainActivity.USERNAME));
                startActivity(intent);
                return true;
            case R.id.action_share_journal:
                shareJournal();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    public void shareJournal(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Journal: "+journalEntity.getContent()+"\n"+
                        "shared from Journaly");
        sendIntent.setType("text/plain");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendIntent);
    }
}
