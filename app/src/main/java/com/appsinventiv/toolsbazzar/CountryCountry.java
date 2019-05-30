package com.appsinventiv.toolsbazzar;

public class CountryCountry {
    String countryName,picUrl;

    public CountryCountry(String countryName, String picUrl) {
        this.countryName = countryName;
        this.picUrl = picUrl;
    }

    public CountryCountry() {
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
