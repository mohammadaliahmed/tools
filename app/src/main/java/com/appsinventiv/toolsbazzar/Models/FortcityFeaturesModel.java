package com.appsinventiv.toolsbazzar.Models;

import android.widget.EditText;

public class FortcityFeaturesModel {
    String promoCode,easyCheckout,cashOnDelivery,onlinePayments,paypalOptions;


    public FortcityFeaturesModel(String promoCode, String easyCheckout, String cashOnDelivery, String onlinePayments, String paypalOptions) {
        this.promoCode = promoCode;
        this.easyCheckout = easyCheckout;
        this.cashOnDelivery = cashOnDelivery;
        this.onlinePayments = onlinePayments;
        this.paypalOptions = paypalOptions;
    }

    public FortcityFeaturesModel() {
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getEasyCheckout() {
        return easyCheckout;
    }

    public void setEasyCheckout(String easyCheckout) {
        this.easyCheckout = easyCheckout;
    }

    public String getCashOnDelivery() {
        return cashOnDelivery;
    }

    public void setCashOnDelivery(String cashOnDelivery) {
        this.cashOnDelivery = cashOnDelivery;
    }

    public String getOnlinePayments() {
        return onlinePayments;
    }

    public void setOnlinePayments(String onlinePayments) {
        this.onlinePayments = onlinePayments;
    }

    public String getPaypalOptions() {
        return paypalOptions;
    }

    public void setPaypalOptions(String paypalOptions) {
        this.paypalOptions = paypalOptions;
    }
}
