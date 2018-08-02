package com.example.leonn.comunicate;

/**
 * Created by jesusgarcia on 2/5/17.
 */

public class Chat {

    private String expertUser;
    private int expertUserID;
    private String user;
    private String lastMessage;
    private int chatID;
    private Boolean inLine;

    public Chat(String expertUser, String user, String last_message, int chatID, int expertID, Boolean inLine){

        this.expertUser = expertUser;
        this.user = user;
        this.lastMessage = last_message;
        this.chatID = chatID;
        this.expertUserID = expertID;
        this.inLine = inLine;
    }

    public Boolean getInLine() {
        return inLine;
    }

    public void setInLine(Boolean inLine) {
        this.inLine = inLine;
    }

    public int getExpertUserID() {
        return expertUserID;
    }

    public void setExpertUserID(int expertUserID) {
        this.expertUserID = expertUserID;
    }
    public int getChatID() { return chatID; }

    public void setChatID(int chatID) {  this.chatID = chatID;  }

    public String getExpertUser() {
        return expertUser;
    }

    public void setExpertUser(String expertUser) {
        this.expertUser = expertUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
