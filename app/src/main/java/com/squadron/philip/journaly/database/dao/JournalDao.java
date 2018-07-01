package com.squadron.philip.journaly.database.dao;

/**
 * Created by philip on 30/06/2018.
 */

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.squadron.philip.journaly.database.entity.JournalEntity;

import java.util.List;

@Dao
public interface JournalDao {
    @Query("Select * from Journal")
    List<JournalEntity> loadAllJournal();

    @Insert
    void InsertJournal(JournalEntity journalEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateJournal(JournalEntity journalEntity);

    @Delete
    void deleteJournal(JournalEntity journalEntity);



}

