package com.squadron.philip.journaly.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.squadron.philip.journaly.database.AppDatabase;
import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.util.List;

/**
 * Created by philip on 02/07/2018.
 */

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<JournalEntity>> journals;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        journals = database.journalDao().loadAllJournal();
    }

    public LiveData<List<JournalEntity>> getJournals(){
        return journals;
    }
}
