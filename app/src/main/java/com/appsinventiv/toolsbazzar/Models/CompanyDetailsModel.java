package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 22/09/2018.
 */

public class CompanyDetailsModel {
    String name,address,telephone,phone,email,coverPicUrl;

    public CompanyDetailsModel(String name, String address, String telephone, String phone, String email) {
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.phone = phone;
        this.email = email;
    }

    public String getCoverPicUrl() {
        return coverPicUrl;
    }

    public void setCoverPicUrl(String coverPicUrl) {
        this.coverPicUrl = coverPicUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CompanyDetailsModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
