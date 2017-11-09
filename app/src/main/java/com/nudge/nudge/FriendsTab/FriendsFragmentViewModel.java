package com.nudge.nudge.FriendsTab;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.Data.NudgeRepository;

import java.util.ArrayList;

/**
 * Created by rushabh on 01/11/17.
 */

public class FriendsFragmentViewModel extends ViewModel{

    private final NudgeRepository mRepository;

    //Firebase instance variables
    private LiveData<ArrayList<DocumentSnapshot>> mFriendsData;

    public FriendsFragmentViewModel(NudgeRepository repository) {
        this.mRepository = repository;
        mFriendsData = mRepository.getFriendsViaQuery();
    }

    public LiveData<ArrayList<DocumentSnapshot>> getFriendsData(){
        return mFriendsData;
    }

    public void requestMoreFriends(){
        mRepository.requestMoreFriends();
    }

    public void changeStar(DocumentSnapshot snapshot, ContactsClass contact){
        mRepository.changeStar(snapshot, contact);
    }

}
