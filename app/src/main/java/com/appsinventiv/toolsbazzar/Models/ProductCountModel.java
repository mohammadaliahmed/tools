package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 20/06/2018.
 */

public class ProductCountModel {
    Product product;
    int quantity;
    long time;
    String size;
    String color;

    public ProductCountModel() {
    }


    public ProductCountModel(Product product, int quantity, long time, String size, String color) {
        this.product = product;
        this.quantity = quantity;
        this.time = time;
        this.size = size;
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
