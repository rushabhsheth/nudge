package com.nudge.nudge.StarContacts;

/**
 * Created by rushabh on 06/10/17.
 */

public class StarContactsClass {

    private static final String TAG = "StarContactsClass";


    private String profile_image_uri;
    private String contact_name;

    boolean mStarPressed;


    public StarContactsClass(){

        this.profile_image_uri = null;
        this.contact_name = null;

        mStarPressed = false;

    }

    public StarContactsClass(String profile_image_uri, String contact_name){
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

    public boolean getStarPressed(){
        return mStarPressed;
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

}
