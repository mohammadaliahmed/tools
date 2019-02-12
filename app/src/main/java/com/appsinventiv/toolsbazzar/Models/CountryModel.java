package com.appsinventiv.toolsbazzar.Models;

import java.util.ArrayList;
import java.util.List;

public class CountryModel {
    String countryName,currencySymbol;
    float currencyRate;
    List<String> provinces;

    public CountryModel(String countryName, String currencySymbol, float currencyRate, List<String> provinces) {
        this.countryName = countryName;
        this.currencySymbol = currencySymbol;
        this.currencyRate = currencyRate;
        this.provinces = provinces;
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
