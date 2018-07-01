package com.squadron.philip.journaly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squadron.philip.journaly.database.AppDatabase;
import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, JournalAdapter.ListItemClickListener {

    private JournalAdapter mAdapter;
    private RecyclerView mJournalList;
    private List<JournalEntity> journals;
    private AppDatabase mAppDataBase;
    public static String EDITOR = "EDITOR";
    private AppExecutors executors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAppDataBase = AppDatabase.getInstance(getApplicationContext());
        executors = new AppExecutors();
        LoadOrReloadAdapter();
        loadEditor();


        mJournalList = (RecyclerView) findViewById(R.id.journal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mJournalList.setLayoutManager(layoutManager);
        mJournalList.setHasFixedSize(true);
        swipeToDelete(mJournalList);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout linearLayout = (LinearLayout)  navigationView.getHeaderView(0).
                findViewById(R.id.side_nav_bar);
        if(getIntent() != null){
            loadSignInUserDetails(linearLayout, getIntent());
        }

    }

    public void swipeToDelete(RecyclerView recyclerView){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<JournalEntity> journalEntities = mAdapter.getJournals();
                        mAppDataBase.journalDao().deleteJournal(journalEntities.get(position));
                        LoadOrReloadAdapter();
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void LoadOrReloadAdapter(){
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                journals = mAppDataBase.journalDao().loadAllJournal();
                mAdapter = new JournalAdapter(journals, MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mJournalList.setAdapter(mAdapter);
                        toggleInfoText();
                    }
                });
            }
        });

    }

    public void toggleInfoText(){
        TextView infoText = (TextView)findViewById(R.id.no_journal);
        if (journals.size() > 0) {
            infoText.setVisibility(View.GONE);
        } else {
            infoText.setVisibility(View.VISIBLE);
        }
    }



    public void loadEditor(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, JournalEditor.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadOrReloadAdapter();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadSignInUserDetails(LinearLayout linearLayout, Intent intent){
        String email = intent.getStringExtra("EMAIL");
        ((TextView)linearLayout.findViewById(R.id.drawer_email_display)).setText(email);

        String name = intent.getStringExtra("USERNAME");
        ((TextView)linearLayout.findViewById(R.id.drawer_name_display)).setText(name);

        Uri photoURL = (Uri)intent.getParcelableExtra("PHOTO_URL");
        ((ImageView)linearLayout.findViewById(R.id.drawer_photo_display)).setImageURI(photoURL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onItemClickListener(JournalEntity journalEntity) {
        Toast.makeText(this, journalEntity.getContent(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, JournalEditor.class);
        intent.putExtra(EDITOR, journalEntity);
        startActivity(intent);
    }
}
