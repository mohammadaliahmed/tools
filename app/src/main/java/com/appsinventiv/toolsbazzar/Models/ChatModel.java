package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 24/06/2018.
 */

public class ChatModel {
    String id,text,username,nameToShow;
    long time;
    String status,initiator;
    String whoReplied;



    public ChatModel(String id, String text, String username, long time, String status, String initiator,String nameToShow,String whoReplied) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.time = time;
        this.status = status;
        this.nameToShow = nameToShow;
        this.initiator = initiator;
        this.whoReplied=whoReplied;
    }


    public ChatModel() {
    }

    public String getWhoReplied() {
        return whoReplied;
    }

    public void setWhoReplied(String whoReplied) {
        this.whoReplied = whoReplied;
    }

    public String getNameToShow() {
        return nameToShow;
    }

    public void setNameToShow(String nameToShow) {
        this.nameToShow = nameToShow;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
