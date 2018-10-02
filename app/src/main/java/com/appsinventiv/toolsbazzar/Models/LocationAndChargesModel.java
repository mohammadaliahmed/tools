package com.appsinventiv.toolsbazzar.Models;

import java.util.List;

/**
 * Created by AliAh on 12/09/2018.
 */

public class LocationAndChargesModel {

    String id, countryName;
    List<String> cities;
    String currency;
    Float currencyRate;
    Float deliveryCharges;
    Float shippingCharges;
    long time;


    public LocationAndChargesModel() {
    }

    public LocationAndChargesModel(String id, String countryName, List<String> cities, String currency, Float currencyRate, Float deliveryCharges, Float shippingCharges, long time) {
        this.id = id;
        this.countryName = countryName;
        this.cities = cities;
        this.currency = currency;
        this.currencyRate = currencyRate;
        this.deliveryCharges = deliveryCharges;
        this.shippingCharges = shippingCharges;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(Float currencyRate) {
        this.currencyRate = currencyRate;
    }

    public Float getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(Float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public Float getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(Float shippingCharges) {
        this.shippingCharges = shippingCharges;
    }
}
