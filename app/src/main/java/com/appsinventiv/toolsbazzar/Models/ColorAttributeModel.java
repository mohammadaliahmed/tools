package com.appsinventiv.toolsbazzar.Models;

public class ColorAttributeModel {
    String color, imageUrl;

    public ColorAttributeModel(String color, String imageUrl) {
        this.color = color;
        this.imageUrl = imageUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
