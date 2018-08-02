package com.example.leonn.comunicate;

/**
 * Created by leonn on 09/05/2017.
 */

public class User {

    private String username;
    private int userID;
    private String accessToken;
    private String email;
    private Boolean inLine;
    private int rating;

    public User(String username, int userID, String accesToken, String email) {
        this.username = username;
        this.userID = userID;
        this.accessToken = accesToken;
        this.email = email;
        this.inLine = false;
    }

    public User(String username, int userID, String accesToken, String email, Boolean inLine, int rating) {
        this.username = username;
        this.userID = userID;
        this.accessToken = accesToken;
        this.email = email;
        this.inLine = inLine;
        this.rating = rating;
    }

    public Boolean getInLine() {
        return inLine;
    }

    public void setInLine(Boolean inLine) {
        this.inLine = inLine;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accesToken) {
        this.accessToken = accesToken;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getRating() {
        return rating;
    }
}
