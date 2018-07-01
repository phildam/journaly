package com.squadron.philip.journaly.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 5/28/2016.
 */
public class Singleton implements Serializable{
    private String errorMsg=" ";
    private boolean isNetworkConnect=false;
    private static Singleton singleton=new Singleton();

    private Singleton(){};

    public static Singleton getInstance(){

        return singleton;
    }


    public void isLoadingList() {

    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isNetworkConnect() {
        return isNetworkConnect;
    }

    public void setNetworkConnect(boolean networkConnect) {
        isNetworkConnect = networkConnect;
    }


}
