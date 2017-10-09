package com.nudge.nudge.NudgesTab;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rushabh on 09/10/17.
 */

public class NudgeClass {

    @SerializedName("sender_name")
    @Expose
    private String sender_name;

    @SerializedName("sender_url")
    @Expose
    private String sender_imageUrl;

    @SerializedName("receiver_name")
    @Expose
    private String receiver_name;

    @SerializedName("receiver_url")
    @Expose
    private String receiver_imageUrl;

    @SerializedName("sender")
    @Expose
    private String sender;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("sent")
    @Expose
    private String sent;

    @SerializedName("received")
    @Expose
    private String received;

    @SerializedName("seen")
    @Expose
    private String seen;

    NudgeClass(){
        this.sender_name = null;
        this.sender_imageUrl = null;
        this.receiver_name = null;
        this.receiver_imageUrl = null;
        this.sender = null;
        this.time = null;
        this.sent = null;
        this.received = null;
        this.seen = null;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String name) {
        this.sender_name = name;
    }

    public String getSender_imageUrl() {
        return sender_imageUrl;
    }

    public void setSender_imageUrl(String imageUrl) {
        this.sender_imageUrl = imageUrl;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String name) {
        this.receiver_name = name;
    }

    public String getReceiver_imageUrl() {
        return receiver_imageUrl;
    }

    public void setReceiver_imageUrl(String imageUrl) {
        this.receiver_imageUrl = imageUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender_bool) {
        this.sender = sender_bool;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.received = seen;
    }

}
