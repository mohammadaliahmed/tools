package com.appsinventiv.toolsbazzar.Models;

import java.util.ArrayList;
import java.util.List;

public class CountryModel {
    String countryName, currencySymbol;
    float currencyRate;
    String mobileCode, imageUrl;
    List<String> provinces;

    boolean isShippingCountry;


    public CountryModel(String countryName, String currencySymbol, float currencyRate, List<String> provinces) {
        this.countryName = countryName;
        this.currencySymbol = currencySymbol;
        this.currencyRate = currencyRate;
        this.provinces = provinces;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isShippingCountry() {
        return isShippingCountry;
    }

    public void setShippingCountry(boolean shippingCountry) {
        isShippingCountry = shippingCountry;
    }

    public CountryModel() {
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public float getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(float currencyRate) {
        this.currencyRate = currencyRate;
    }

    public List<String> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<String> provinces) {
        this.provinces = provinces;
    }
}
