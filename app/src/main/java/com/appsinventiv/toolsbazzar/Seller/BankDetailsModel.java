package com.appsinventiv.toolsbazzar.Seller;

public class BankDetailsModel {
    String userId;
    String beneficiaryName,bankName,accountNumber;

    public BankDetailsModel(String userId, String beneficiaryName, String bankName, String accountNumber) {
        this.userId = userId;
        this.beneficiaryName = beneficiaryName;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    public BankDetailsModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
