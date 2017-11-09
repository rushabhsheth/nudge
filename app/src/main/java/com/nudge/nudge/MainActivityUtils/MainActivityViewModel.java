package com.nudge.nudge.MainActivityUtils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nudge.nudge.Data.NudgeRepository;

/**
 * Created by rushabh on 20/10/17.
 */

public class MainActivityViewModel extends ViewModel {

    private final NudgeRepository mRepository;
    private boolean mIsSigningIn;

    //Firebase instance variables
    private LiveData<FirebaseUser> mFirebaseUser;

    public MainActivityViewModel(NudgeRepository repository) {
        this.mRepository = repository;
        mIsSigningIn = false;
        mFirebaseUser = mRepository.getFirebaseUser();
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public LiveData<FirebaseUser> getFirebaseUser(){
        return mFirebaseUser;
    }

    public void startSignOut(){
        mRepository.startSignOut();
    }

    public void setContactsPermissionGranted(boolean bool){
        if(bool) {
            mRepository.syncPhoneContactsWithServer();
        }
    }

}
