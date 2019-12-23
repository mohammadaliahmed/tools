package com.appsinventiv.toolsbazzar.Models;

public class NewProductModel {
    String sku, color, size;
    int qty, wholesalePrice, oldWholesalePrice, minOrderQuantity, retailPrice, oldRetailPrice;

    public NewProductModel(String sku, String color, String size, int qty, int wholesalePrice, int oldWholesalePrice, int minOrderQuantity, int retailPrice, int oldRetailPrice) {
        this.sku = sku;
        this.color = color;
        this.size = size;
        this.qty = qty;
        this.wholesalePrice = wholesalePrice;
        this.oldWholesalePrice = oldWholesalePrice;
        this.minOrderQuantity = minOrderQuantity;
        this.retailPrice = retailPrice;
        this.oldRetailPrice = oldRetailPrice;
    }


    public NewProductModel() {
    }

    public NewProductModel(NewProductModel newProductModel) {
        this.sku = newProductModel.getSku();
        this.color = newProductModel.getColor();
        this.size = newProductModel.getSize();
        this.qty = newProductModel.getQty();
        this.wholesalePrice = newProductModel.getWholesalePrice();
        this.oldWholesalePrice = newProductModel.getOldWholesalePrice();
        this.minOrderQuantity = newProductModel.getMinOrderQuantity();
        this.retailPrice = newProductModel.getRetailPrice();
        this.oldRetailPrice = newProductModel.getOldRetailPrice();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(int wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public int getOldWholesalePrice() {
        return oldWholesalePrice;
    }

    public void setOldWholesalePrice(int oldWholesalePrice) {
        this.oldWholesalePrice = oldWholesalePrice;
    }

    public int getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(int minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public int getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(int retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getOldRetailPrice() {
        return oldRetailPrice;
    }

    public void setOldRetailPrice(int oldRetailPrice) {
        this.oldRetailPrice = oldRetailPrice;
    }
}
