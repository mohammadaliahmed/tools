package com.appsinventiv.toolsbazzar.Models;

import java.util.ArrayList;
import java.util.HashMap;

public class NewCartModel {
    String name;
    double shippingCharges;
    double deliveryCharges;
    double total;
    ArrayList<ProductCountModel> list;
    VendorModel vendorModel;

    public NewCartModel(String name, double shippingCharges, double deliveryCharges, double total,
                        ArrayList<ProductCountModel> list,
                        VendorModel vendorModel) {
        this.name = name;
        this.shippingCharges = shippingCharges;
        this.deliveryCharges = deliveryCharges;
        this.total = total;
        this.list = list;
        this.vendorModel = vendorModel;
    }

    public VendorModel getVendorModel() {
        return vendorModel;
    }

    public void setVendorModel(VendorModel vendorModel) {
        this.vendorModel = vendorModel;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(double shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ProductCountModel> getList() {
        return list;
    }

    public void setList(ArrayList<ProductCountModel> list) {
        this.list = list;
    }
}
