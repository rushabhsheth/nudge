package com.nudge.nudge.ContactsData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class ContactsClass {

    private static final String TAG = "ContactsClass";

    private int mId;
    private List<String> profileImageList;
    private String profile_image_uri;
    private String contact_name;

    boolean mStarPressed;


    public ContactsClass(){

        this.mId = 0;
        this.profile_image_uri = null;
        this.contact_name = null;
        this.profileImageList = new ArrayList<>();
        this.mStarPressed = false;

    }

    public ContactsClass(String profile_image_uri, String contact_name){
        this.profile_image_uri = profile_image_uri;
        this.contact_name = contact_name;
        this.mStarPressed = false;
    }

    //Getter Functions

    public String getProfile_image_uri() {
        return profile_image_uri;
    }

    public String getContact_name(){
        return contact_name;
    }

    public int getId(){
        return mId;
    }

    public boolean getStarPressed(){
        return mStarPressed;
    }

    public List<String> getProfileImageList() {
        return profileImageList;
    }

    //Setter Functions
    public void setProfile_image_uri(String profile_image_uri){
        this.profile_image_uri = profile_image_uri;
    }

    public void setContact_name(String contact_name){
        this.contact_name = contact_name;
    }

    public void setStarPressed(boolean pressed){
        this.mStarPressed = pressed;
    }

    public void setProfileImageList (List<String> profileImageList) {
        this.profileImageList = profileImageList;
    }

    public void setId (int id){
        this.mId = id;
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
