package com.squadron.philip.journaly.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.squadron.philip.journaly.Processor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by philip on 30/06/2018.
 */

@Entity(tableName = "Journal")
public class JournalEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String content = "";
    private String location = "";
    private Date dateAdded = null;
    private Date lastModifiedDate = new Date();
    private String imageUrl = "";
    private String time = "";
    private String dayOfWeek = new Processor().getDayOfWeek(new Date());

    @Ignore
    public JournalEntity(){

    }

    @Ignore
    public JournalEntity(String content, Date dateAdded, String location,
                         Date lastModifiedDate, String time, String dayOfWeek, String imageUrl){
        this.content = content;
        this.location = location;
        this.dateAdded = dateAdded;
        this.lastModifiedDate = lastModifiedDate;
        this.imageUrl = imageUrl;
        this.setTime(time);
        this.setDayOfWeek(dayOfWeek);
    }

    public JournalEntity(int id, String content, Date dateAdded, String location,
                         Date lastModifiedDate,String time, String dayOfWeek, String imageUrl){
        this.content = content;
        this.id = id;
        this.location = location;
        this.dateAdded = dateAdded;
        this.lastModifiedDate = lastModifiedDate;
        this.imageUrl = imageUrl;
        this.setTime(time);
        this.setDayOfWeek(dayOfWeek);
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
