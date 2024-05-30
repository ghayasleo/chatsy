package com.example.chatsy.models;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phoneNumber;
    private String userName;
    private Timestamp createdTimeStamp; // we'll know when the account was created
    private String userId;
    private String fcmToken;

    public UserModel() {
    }

    public UserModel(String phoneNumber, String userName, Timestamp createdTimeStamp, String userId) {
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.createdTimeStamp = createdTimeStamp;
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
