package com.squadron.philip.journaly.model;

import java.io.Serializable;

/**
 * Created by g33k5qu4d on 8/29/2016.
 */
public class NoteModel implements Serializable{
    public static final String NOTECONSTANT="NOTE";
    private String title;
    private String Content;
    private String datecreated;
    private String datemodified;
    private String alarm;
    private int id;

    public NoteModel(){

    }

    public NoteModel(String title, String date){
        this.datecreated=date;
        this.title=title;
    }

    public NoteModel(int id, String title, String content, String datecreated, String dateModified){
        this.datecreated=datecreated;
        this.title=title;
        this.Content=content;
        this.id=id;
        this.setDatemodified(dateModified);
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getDate() {
        return datecreated;
    }

    public void setDate(String date) {
        this.datecreated = date;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatemodified() {
        return datemodified;
    }

    public void setDatemodified(String datemodified) {
        this.datemodified = datemodified;
    }
}
