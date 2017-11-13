package com.nudge.nudge.Login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.Data.NudgeRepository;

import java.util.ArrayList;

/**
 * Created by rushabh on 13/11/17.
 */

public class LoginActivityViewModel extends ViewModel {

    private final NudgeRepository mRepository;
    private boolean mIsSigningIn;

    //Firebase instance variables
    private LiveData<FirebaseUser> mFirebaseUser;

    public LoginActivityViewModel(NudgeRepository repository) {
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


}
