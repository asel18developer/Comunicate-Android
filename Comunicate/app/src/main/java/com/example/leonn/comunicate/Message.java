package com.example.leonn.comunicate;

import android.util.Log;

/**
 * Created by leonn on 24/05/2017.
 */

public class Message {

    private int userID;
    private String message;
    private Boolean isMine;
    private String date;

    public Message( int userID, String message, Boolean isMine){

        this.userID = userID;
        this.message = message;
        this.isMine = isMine;

    }

    public Message( int userID, String message, Boolean isMine, String date){

        this.userID = userID;
        this.message = message;
        this.isMine = isMine;
        this.date = autoFormat(date);


    }

    private String autoFormat(String date) {

        String dateFormat = "";
        dateFormat = date.split(" ")[1];
        dateFormat = dateFormat.substring(0,5);
        return dateFormat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUser() {
        return userID;
    }

    public void setUser(int user) {
        this.userID = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getMine() {
        return isMine;
    }

    public void setMine(Boolean mine) {
        isMine = mine;
    }




}
