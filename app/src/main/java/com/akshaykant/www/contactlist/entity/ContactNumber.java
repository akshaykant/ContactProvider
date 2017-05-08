package com.akshaykant.www.contactlist.entity;

/**
 * Created by Akshay Kant on 29-04-2017.
 */

public class ContactNumber {

    private String mobileNumber;

    private String countryCode;

    private String number;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
