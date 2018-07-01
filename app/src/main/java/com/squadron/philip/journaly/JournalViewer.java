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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_adder);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        journaltext = (TextView) findViewById(R.id.journal_viewer_text);

        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayHomeAsUpEnabled(true);

        journalEntity = (JournalEntity)getIntent().getSerializableExtra(MainActivity.EDITOR);
        journaltext.setText(journalEntity.getContent());
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
                startActivity(intent);
                return true;
            case R.id.action_share_journal:

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
