package com.nudge.nudge.ContactsData;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by rushabh on 06/10/17.
 */

public class ContactsClass {

    private static final String TAG = "ContactsClass";

    public static final String ACCOUNT_TYPE = "accountType";
    public static final String CONTACT_ID = "contactId";
    public static final String CONTACT_NAME = "contactName";
    public static final String CONTACT_NUMBER = "contactNumber";
    public static final String LAST_TIME_CONTACTED = "lastTimeContacted";
    public static final String PROFILE_IMAGE_LIST = "profileImageList";
    public static final String PROFILE_IMAGE_URI = "profileImageUri";
    public static final String STARRED = "starred";
    public static final String TIMES_CONTACTED = "timesContacted";
    public static final String TIMESTAMP = "timestamp";
    public static final String WHATSAPP_FRIENDS = "whatsapp_friends";

    private String contact_name;
    private long contact_id;
    private String contact_number;
    private int times_contacted;
    private long last_time_contacted;
    private int starred;
    private String profile_image_uri;
    private List<String> profile_image_list;
    private String account_type;
    private @ServerTimestamp Date timestamp;

    public ContactsClass(){


        this.contact_id = 0;
        this.times_contacted = 0;
        this.last_time_contacted = 0;
        this.starred = 0;
        this.profile_image_uri = null;
        this.profile_image_list = new ArrayList<>();
        this.contact_name = null;
        this.account_type = null;
        this.contact_number = null;
    }

    public ContactsClass(String profile_image_uri, String contact_name){
        this.profile_image_uri = profile_image_uri;
        this.contact_name = contact_name;
    }

    public String getContactName(){
        return contact_name;
    }

    public void setContactName(String contact_name){
        this.contact_name = contact_name;
    }

    public long getContactId(){
        return contact_id;
    }

    public void setContactId(long contact_id){
        this.contact_id = contact_id;
    }

    public String getContactNumber(){
        return contact_number;
    }

    public void setContactNumber(String contact_number){
        this.contact_number = contact_number;
    }

    public int getTimesContacted(){
        return times_contacted;
    }

    public void setTimesContacted(int times_contacted){
        this.times_contacted = times_contacted;
    }

    public long getLastTimeContacted(){
        return last_time_contacted;
    }

    public void setLastTimeContacted(long last_time_contacted){
        this.last_time_contacted = last_time_contacted;
    }

    public int getStarred(){
        return starred;
    }

    public void setStarred(int pressed){
        this.starred = pressed;
    }

    public String getProfileImageUri(){
        return profile_image_uri;
    }

    public void setProfileImageUri(String profile_image_uri){
        this.profile_image_uri = profile_image_uri;
    }

    public List<String> getProfileImageList() {
        return profile_image_list;
    }


    public void setProfileImageList (List<String> profileImageList) {
        this.profile_image_list = profileImageList;
    }

    public String getAccountType(){
        return account_type;
    }

    public void setAccountType(String account_type){
        this.account_type = account_type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactsClass that = (ContactsClass) o;

        return contact_name.equals(that.contact_name);

    }

    @Override
    public int hashCode() {
        return contact_name.hashCode();
    }

}
