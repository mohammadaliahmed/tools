package com.appsinventiv.toolsbazzar.Seller.SellerOrders;

public class ShippingCompanyModel {
    String id,name,telephone,address,email,picUrl;

    public ShippingCompanyModel(String id, String name, String telephone, String address, String email, String picUrl) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.address = address;
        this.email = email;
        this.picUrl = picUrl;
    }

    public ShippingCompanyModel() {
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}
