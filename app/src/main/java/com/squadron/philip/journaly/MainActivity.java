package com.squadron.philip.journaly;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squadron.philip.journaly.database.AppDatabase;
import com.squadron.philip.journaly.database.entity.JournalEntity;
import com.squadron.philip.journaly.model.MainViewModel;
import com.squadron.philip.journaly.service.FireBaseBackUpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, JournalAdapter.ListItemClickListener {

    private JournalAdapter mAdapter;
    private List<JournalEntity> journals = new ArrayList<>();;
    private AppDatabase mAppDataBase;
    public static String EDITOR = "EDITOR";
    public static String USERNAME = "USERNAME";
    private AppExecutors executors;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Fire base service for backing up journals
        FireBaseBackUpService.backUpJournal(this);

        mAppDataBase = AppDatabase.getInstance(getApplicationContext());
        RecyclerView journalRecyclerView = (RecyclerView) findViewById(R.id.journal_recycler_view);
        mAdapter = new JournalAdapter(journals, this);
        executors = new AppExecutors();

        Singleton.getInstance().setUserName(getIntent().getStringExtra(MainActivity.USERNAME));

        loadActionBarAndDrawer();
        initJournalRecycler(journalRecyclerView, mAdapter);


        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        final Observer<List<JournalEntity>> jouListObserver = new Observer<List<JournalEntity>>() {
            @Override
            public void onChanged(@Nullable List<JournalEntity> journalEntities) {
                mAdapter.setJournals(journalEntities);
                toggleInfoText(journalEntities);
            }
        };

        mainViewModel.getJournals().observe(this, jouListObserver);

        openJournalViewer();

    }

    public void initJournalRecycler(RecyclerView journalRecyclerView, JournalAdapter adapter){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        journalRecyclerView.setLayoutManager(layoutManager);
        journalRecyclerView.setHasFixedSize(true);
        journalRecyclerView.setAdapter(mAdapter);
        swipeToDelete(journalRecyclerView);
    }

    public void loadActionBarAndDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
    }


    public void toggleInfoText(List<JournalEntity> journalEntities){
        TextView infoText = (TextView)findViewById(R.id.no_journal);
        if (journalEntities.size() > 0) {
            infoText.setVisibility(View.GONE);
        } else {
            infoText.setVisibility(View.VISIBLE);
        }
    }


    public void openJournalViewer(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, JournalEditor.class);
                intent.putExtra(USERNAME,  getIntent().getStringExtra("USERNAME"));
                startActivity(intent);
            }
        });
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

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClickListener(JournalEntity journalEntity) {
        Intent intent = new Intent(MainActivity.this, JournalViewer.class);
        intent.putExtra(EDITOR, journalEntity);
        intent.putExtra(USERNAME,  getIntent().getStringExtra("USERNAME"));
        startActivity(intent);
    }
}
