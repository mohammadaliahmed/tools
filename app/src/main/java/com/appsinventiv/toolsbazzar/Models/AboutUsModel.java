package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 01/09/2018.
 */

public class AboutUsModel {
    String about,vision,mission,values,contact;


    public AboutUsModel(String about, String vision, String mission, String values, String contact) {
        this.about = about;
        this.vision = vision;
        this.mission = mission;
        this.values = values;
        this.contact = contact;
    }

    public AboutUsModel() {
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
