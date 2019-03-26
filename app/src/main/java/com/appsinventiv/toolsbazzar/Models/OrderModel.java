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
    String invoicePath;
    String orderFor;


    boolean isInvoiced;
    long invoiceNumber;

    String trackingNumber;
    String carrier;
    String deliveryBy,receiverName,receiverNameCredit,creditDueDate;



    public OrderModel() {
    }

    public OrderModel(String orderId, Customer customer, ArrayList<ProductCountModel> countModelArrayList,
                      float totalPrice, long time, String instructions, String date,
                      String chosenTime,String orderStatus,Float shippingCharges,Float deliveryCharges,String orderFor) {
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
        this.orderFor=orderFor;
    }

    public boolean isInvoiced() {
        return isInvoiced;
    }

    public void setInvoiced(boolean invoiced) {
        isInvoiced = invoiced;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getDeliveryBy() {
        return deliveryBy;
    }

    public void setDeliveryBy(String deliveryBy) {
        this.deliveryBy = deliveryBy;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverNameCredit() {
        return receiverNameCredit;
    }

    public void setReceiverNameCredit(String receiverNameCredit) {
        this.receiverNameCredit = receiverNameCredit;
    }

    public String getCreditDueDate() {
        return creditDueDate;
    }

    public void setCreditDueDate(String creditDueDate) {
        this.creditDueDate = creditDueDate;
    }

    public String getOrderFor() {
        return orderFor;
    }

    public void setOrderFor(String orderFor) {
        this.orderFor = orderFor;
    }

    public String getInvoicePath() {
        return invoicePath;
    }

    public void setInvoicePath(String invoicePath) {
        this.invoicePath = invoicePath;
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

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }
}