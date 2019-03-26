package com.appsinventiv.toolsbazzar.Models;

import java.util.ArrayList;

/**
 * Created by AliAh on 03/09/2018.
 */

public class InvoiceModel {
    long id;
    ArrayList<ProductCountModel> countModelArrayList;
    ArrayList<ProductCountModel> newCountModelArrayList;
    Customer customer;
    float totalPrice;
    long time;
    String orderId;
    float deliveryCharges;
    float shippingCharges;
    float grandTotal;
    String orderStatus, deliveryBy="";
    int orderItems;
    int outOfStock;
    String invoiceStatus;

    public InvoiceModel() {
    }
    public InvoiceModel(long id,
                        ArrayList<ProductCountModel> countModelArrayList,
                        ArrayList<ProductCountModel> newCountModelArrayList,
                        Customer customer,
                        float totalPrice,
                        long time,
                        String orderId,
                        float deliveryCharges,
                        float shippingCharges,
                        float grandTotal,
                        String orderStatus,
                        int orderItems,
                        String deliveryBy,
                        int outOfStock,
                        String invoiceStatus
    ) {
        this.id = id;
        this.countModelArrayList = countModelArrayList;
        this.newCountModelArrayList = newCountModelArrayList;
        this.customer = customer;
        this.totalPrice = totalPrice;
        this.time = time;
        this.orderId = orderId;
        this.deliveryCharges = deliveryCharges;
        this.shippingCharges = shippingCharges;
        this.grandTotal = grandTotal;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
        this.deliveryBy = deliveryBy;
        this.outOfStock = outOfStock;
        this.invoiceStatus=invoiceStatus;
    }



    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(int orderItems) {
        this.orderItems = orderItems;
    }

    public String getDeliveryBy() {
        return deliveryBy;
    }

    public void setDeliveryBy(String deliveryBy) {
        this.deliveryBy = deliveryBy;
    }

    public int getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(int outOfStock) {
        this.outOfStock = outOfStock;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<ProductCountModel> getCountModelArrayList() {
        return countModelArrayList;
    }

    public void setCountModelArrayList(ArrayList<ProductCountModel> countModelArrayList) {
        this.countModelArrayList = countModelArrayList;
    }

    public ArrayList<ProductCountModel> getNewCountModelArrayList() {
        return newCountModelArrayList;
    }

    public void setNewCountModelArrayList(ArrayList<ProductCountModel> newCountModelArrayList) {
        this.newCountModelArrayList = newCountModelArrayList;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public float getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public float getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(float shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(float grandTotal) {
        this.grandTotal = grandTotal;
    }
}
