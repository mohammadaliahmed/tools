package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

public class BottomDialogModel {
    String id, name,subtitle,picUrl;

    public BottomDialogModel(String id, String name, String subtitle, String picUrl) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.picUrl = picUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}


