package com.squadron.philip.journaly.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by philip on 30/06/2018.
 */

@Entity(tableName = "Journal")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String content;
    private String location;
    private Date dateAdded;
    private Date lastModifiedDate;
    private String imageUrl;

    @Ignore
    public JournalEntry(String content, Date dateAdded, String location,
                        Date lastModifiedDate, String imageUrl){
        this.content = content;
        this.location = location;
        this.dateAdded = dateAdded;
        this.lastModifiedDate = lastModifiedDate;
        this.imageUrl = imageUrl;
    }

    public JournalEntry(int id, String content, Date dateAdded, String location,
                        Date lastModifiedDate, String imageUrl){
        this.content = content;
        this.id = id;
        this.location = location;
        this.dateAdded = dateAdded;
        this.lastModifiedDate = lastModifiedDate;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
