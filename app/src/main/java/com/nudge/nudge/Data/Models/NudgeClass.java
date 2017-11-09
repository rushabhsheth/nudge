package com.nudge.nudge.Data.Models;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by rushabh on 09/10/17.
 */

public class NudgeClass {

    @SerializedName("senderName")
    @Expose
    private String senderName;

    @SerializedName("senderImageUrl")
    @Expose
    private String senderImageUrl;

    @SerializedName("receiverName")
    @Expose
    private String receiverName;

    @SerializedName("receiverImageUrl")
    @Expose
    private String receiverImageUrl;

    @SerializedName("senderId")
    @Expose
    private String senderId;

    @SerializedName("receiverId")
    @Expose
    private String receiverId;

    @SerializedName("timestamp")
    @Expose
    private @ServerTimestamp String timestamp;

    @SerializedName("status")
    @Expose
    private String status;

    public NudgeClass(String senderName, String senderImageUrl, String receiverName, String receiverImageUrl, String senderId, String receiverId, String timestamp, String status) {
        this.senderName = senderName;
        this.senderImageUrl = senderImageUrl;
        this.receiverName = receiverName;
        this.receiverImageUrl = receiverImageUrl;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.status = status;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverImageUrl() {
        return receiverImageUrl;
    }

    public void setReceiverImageUrl(String receiverImageUrl) {
        this.receiverImageUrl = receiverImageUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
