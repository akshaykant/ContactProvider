package com.akshaykant.www.contactlist.entity;

import java.util.HashSet;

/**
 * Created by Akshay Kant on 29-04-2017.
 */

public class Contact {

    private String contactId;

    private String displayName;

    private String imageUri;

    private HashSet<ContactNumber> contactNumber;

    private HashSet<String> email;

    public Contact() {
        this.contactNumber = new HashSet<ContactNumber>();
        this.email = new HashSet<String>();

    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public HashSet<ContactNumber> getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(HashSet<ContactNumber> contactNumber) {
        this.contactNumber = contactNumber;
    }

    public HashSet<String> getEmail() {
        return email;
    }

    public void setEmail(HashSet<String> email) {
        this.email = email;
    }
}
