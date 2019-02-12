package com.appsinventiv.toolsbazzar.Models;

public class CityDeliveryChargesModel {
    String cityName,oneKg,halfKg;

    public CityDeliveryChargesModel(String cityName, String oneKg, String halfKg) {
        this.cityName = cityName;
        this.oneKg = oneKg;
        this.halfKg = halfKg;
    }

    public CityDeliveryChargesModel() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOneKg() {
        return oneKg;
    }

    public void setOneKg(String oneKg) {
        this.oneKg = oneKg;
    }

    public String getHalfKg() {
        return halfKg;
    }

    public void setHalfKg(String halfKg) {
        this.halfKg = halfKg;
    }
}
