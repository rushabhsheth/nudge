package com.nudge.nudge.Data.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by rushabh on 21/10/17.
 */

public class UserClass {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String fcmtoken;
    private int numberWhatsappFriends;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getNumberWhatsappFriends() {
        return numberWhatsappFriends;
    }

    public void setNumberWhatsappFriends(int numberWhatsappFriends) {
        this.numberWhatsappFriends = numberWhatsappFriends;
    }

}
