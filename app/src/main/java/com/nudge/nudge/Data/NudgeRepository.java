package com.nudge.nudge.Data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nudge.nudge.Data.Network.FirebaseDataSource;
import com.nudge.nudge.Utilities.AppExecutors;

/**
 * Created by rushabh on 01/11/17.
 */

public class NudgeRepository {
    private static final String LOG_TAG = NudgeRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NudgeRepository sInstance;
    private final FirebaseDataSource mFirebaseDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private NudgeRepository(FirebaseDataSource firebaseDataSource,
                               AppExecutors executors) {
        mFirebaseDataSource = firebaseDataSource;
        mExecutors = executors;

    }

    public synchronized static NudgeRepository getInstance(
            FirebaseDataSource firebaseDataSource,
            AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NudgeRepository(firebaseDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    private synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;
    }

    public LiveData<FirebaseUser> getFirebaseUser(){
        return mFirebaseDataSource.getFirebaseUser();
    }

    public void startSignOut(){
        mFirebaseDataSource.startSignOut();
    }

}
