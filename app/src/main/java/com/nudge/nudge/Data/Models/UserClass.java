package com.nudge.nudge.Data.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by rushabh on 21/10/17.
 */

public class UserClass {

    private String userName;
    private String userId;
    private String userIdentifier;
    private String FCMToken;
    private @ServerTimestamp Date timestamp;

    public UserClass() {
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
