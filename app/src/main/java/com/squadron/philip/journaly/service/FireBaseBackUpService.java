package com.squadron.philip.journaly.service;

import android.app.IntentService;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squadron.philip.journaly.MainActivity;
import com.squadron.philip.journaly.Singleton;
import com.squadron.philip.journaly.database.AppDatabase;
import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FireBaseBackUpService extends IntentService {

    private static final String ACTION_BACKUP_JOURNAL = "com.squadron.philip.journaly.service.action.BACKUP";
    private static final String ACTION_NOTIFICATION = "com.squadron.philip.journaly.service.action.NOTIFICATION";
    private static final String ACTION_REMINDER = "com.squadron.philip.journaly.service.action.REMINDER";

    private static final String EXTRA_SINGLETON = "com.squadron.philip.journaly.service.extra.SINGLETON";
    private static final String EXTRA_USER_NAME = "com.squadron.philip.journaly.service.extra.EXTRA_USER_NAME";
    private static final String EXTRA_BACK_UP_DATA = "com.squadron.philip.journaly.service.extra.EXTRA_BACK_UP_DATA";
    private static final String EXTRA_REMINDER = "com.squadron.philip.journaly.service.extra.EXTRA_REMINDER";

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static AppDatabase appDatabase;

    public FireBaseBackUpService() {
        super("FireBaseBackUpService");
    }


    public static void backUpJournal(Context context) {
        Intent intent = new Intent(context, FireBaseBackUpService.class);
        appDatabase = AppDatabase.getInstance(context);
        Toast.makeText(context, "Retrieving data from Firebase", Toast.LENGTH_SHORT).show();
        intent.setAction(ACTION_BACKUP_JOURNAL);
        Log.e("USERNAME", Singleton.getInstance().getUserName());
        intent.putExtra(EXTRA_USER_NAME, Singleton.getInstance().getUserName());
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BACKUP_JOURNAL.equals(action)) {
                handleBackUp(intent);
            }
        }
    }


    private void handleBackUp(Intent intent) {
        final String userName = intent.getStringExtra(EXTRA_USER_NAME);

        LiveData<List<JournalEntity>> journalEntityLiveData =
                appDatabase.journalDao().loadAllJournal();

        Log.e("Processing in bg", "loading");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Journaly");
        myRef.child(userName).setValue(journalEntityLiveData.getValue());

    }



}
