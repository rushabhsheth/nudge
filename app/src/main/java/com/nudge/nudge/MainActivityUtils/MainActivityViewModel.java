package com.nudge.nudge.MainActivityUtils;

import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by rushabh on 20/10/17.
 */

public class MainActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;
    private FirebaseUser mUser;

    public MainActivityViewModel() {
        mIsSigningIn = false;
        mUser = null;
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public FirebaseUser getUser(){
        return mUser;
    }

    public void setUser(FirebaseUser user){
        this.mUser = user;
    }

}
