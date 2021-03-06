package com.squadron.philip.journaly.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.squadron.philip.journaly.Utils;
import com.squadron.philip.journaly.model.JournalModel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by philip on 30/06/2018.
 */

@Entity(tableName = "Journal")
public class JournalEntity implements Serializable, JournalModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String content = "";
    private String location = "";
    private String dateAdded = null;
    private String lastModifiedDate = new Date().toLocaleString();
    private String imageUrl = "";
    private String time = "";
    private String dayOfWeek = "";


    @Ignore
    public JournalEntity(){

    }

    @Ignore
    public JournalEntity(String content, String dateAdded, String location,
                         String lastModifiedDate, String time, String dayOfWeek, String imageUrl){
        this.content = content;
        this.location = location;
        this.dateAdded = dateAdded;
        this.lastModifiedDate = lastModifiedDate;
        this.imageUrl = imageUrl;
        this.setTime(time);
        this.setDayOfWeek(dayOfWeek);
    }

    public JournalEntity(int id, String content, String dateAdded, String location,
                         String lastModifiedDate,String time, String dayOfWeek, String imageUrl){
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

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
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
