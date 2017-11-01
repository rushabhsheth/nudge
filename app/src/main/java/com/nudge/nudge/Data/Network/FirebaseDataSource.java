package com.nudge.nudge.Data.Network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nudge.nudge.Utilities.AppExecutors;

import java.util.concurrent.TimeUnit;

/**
 * Created by rushabh on 01/11/17.
 */

public class FirebaseDataSource {
    private static final String LOG_TAG = FirebaseDataSource.class.getSimpleName();

    // Interval at which to sync contacts with the firestore. Use TimeUnit for convenience, rather than
    // writing out a bunch of multiplication ourselves and risk making a silly mistake.
    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String FIREBASE_SYNC_TAG = "firebase-sync";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static FirebaseDataSource sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded weather forecasts
    private final AppExecutors mExecutors;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private MutableLiveData<FirebaseUser> mFirebaseUser;

    private FirebaseDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;

        mFirebaseUser = new MutableLiveData<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        initAuthListener();

    }

    /**
     * Get the singleton for this class
     */
    public static FirebaseDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseDataSource(context.getApplicationContext(), executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    private void initAuthListener() {
        //Firebase Auth State Listener

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mExecutors.mainThread().execute(() -> {
                    mFirebaseUser.postValue(firebaseAuth.getCurrentUser());
                });
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public LiveData<FirebaseUser> getFirebaseUser(){
        return mFirebaseUser;
    }

    public void startSignOut(){
        mFirebaseAuth.signOut();
    }

}
