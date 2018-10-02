package com.appsinventiv.toolsbazzar.Models;

import java.util.ArrayList;

/**
 * Created by AliAh on 20/06/2018.
 */

public class OrderModel {
    String orderId;
    long time;
    Customer customer;
    ArrayList<ProductCountModel> countModelArrayList;
    float totalPrice;
    String instructions;
    String date,chosenTime;
    String orderStatus;
    Float shippingCharges,deliveryCharges;



    public OrderModel() {
    }

    public OrderModel(String orderId, Customer customer, ArrayList<ProductCountModel> countModelArrayList,
                      float totalPrice, long time, String instructions, String date,
                      String chosenTime,String orderStatus,Float shippingCharges,Float deliveryCharges) {
        this.orderId = orderId;
        this.time = time;
        this.customer = customer;
        this.countModelArrayList = countModelArrayList;
        this.totalPrice = totalPrice;
        this.instructions = instructions;
        this.date = date;
        this.chosenTime = chosenTime;
        this.orderStatus=orderStatus;
        this.deliveryCharges=deliveryCharges;
        this.shippingCharges=shippingCharges;
    }

    public Float getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(Float shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public Float getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(Float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChosenTime() {
        return chosenTime;
    }

    public void setChosenTime(String chosenTime) {
        this.chosenTime = chosenTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ArrayList<ProductCountModel> getCountModelArrayList() {
        return countModelArrayList;
    }

    public void setCountModelArrayList(ArrayList<ProductCountModel> countModelArrayList) {
        this.countModelArrayList = countModelArrayList;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}