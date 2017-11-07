package com.nudge.nudge.NudgesTab;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.nudge.nudge.Data.Database.ContactsClass;
import com.nudge.nudge.Data.NudgeRepository;

import java.util.ArrayList;

/**
 * Created by rushabh on 07/11/17.
 */

public class NudgesFragmentViewModel extends ViewModel {

    private final NudgeRepository mRepository;

    //Firebase instance variables
    private LiveData<ArrayList<DocumentSnapshot>> mNudgesData;

    public NudgesFragmentViewModel(NudgeRepository repository) {
        this.mRepository = repository;
        mNudgesData = mRepository.getNudges();
    }

    public LiveData<ArrayList<DocumentSnapshot>> getNudgesData() {
        return mNudgesData;
    }

}