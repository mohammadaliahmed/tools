package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 26/06/2018.
 */

public class GroceryListModel {
    String title;
    int imageId;

    public GroceryListModel(String title, int imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
