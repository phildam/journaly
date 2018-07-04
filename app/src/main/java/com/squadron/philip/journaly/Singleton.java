package com.squadron.philip.journaly;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by philip on 04/07/2018.
 */

public class Singleton implements Serializable {
    private static final Singleton ourInstance = new Singleton();
    private Context context = null;
    private String userName = "";

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {

    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
