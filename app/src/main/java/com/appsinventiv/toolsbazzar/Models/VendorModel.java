package com.appsinventiv.toolsbazzar.Models;

/**
 * Created by AliAh on 23/08/2018.
 */

public class VendorModel {
    String vendorId,vendorName, username, email, password, phone,telNumber,address, city,country,fcmKey;
    long time;
    String customerType;
    String storeName,businessRegistrationNumber;
    String locationId;
    String currencySymbol;
    Float currencyRate;
    String province,district;
    boolean status;
    String bankAccount;
    String paypalId;
    int code;
    boolean codeVerified;

    public VendorModel(String vendorId, String vendorName, String username, String email, String password,
                       String phone, String telNumber, String address, String city, String country, String fcmKey,
                       long time, String customerType, String storeName, String businessRegistrationNumber,
                       String locationId, String currencySymbol, Float currencyRate, String province,
                       String district,boolean status,int code,boolean codeVerified) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.telNumber = telNumber;
        this.address = address;
        this.city = city;
        this.country = country;
        this.fcmKey = fcmKey;
        this.time = time;
        this.customerType = customerType;
        this.storeName = storeName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.locationId = locationId;
        this.currencySymbol = currencySymbol;
        this.currencyRate = currencyRate;
        this.province = province;
        this.status=status;
        this.district=district;
        this.code=code;
        this.codeVerified=codeVerified;

    }

    public boolean isCodeVerified() {
        return codeVerified;
    }

    public void setCodeVerified(boolean codeVerified) {
        this.codeVerified = codeVerified;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getPaypalId() {
        return paypalId;
    }

    public void setPaypalId(String paypalId) {
        this.paypalId = paypalId;
    }

    public boolean isActive() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public VendorModel() {
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Float getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(Float currencyRate) {
        this.currencyRate = currencyRate;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
