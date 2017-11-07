package com.nudge.nudge.Data.Network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nudge.nudge.Data.Database.ContactsClass;
import com.nudge.nudge.Data.Database.UserClass;
import com.nudge.nudge.FriendsTab.FriendsCard;
import com.nudge.nudge.R;
import com.nudge.nudge.Utilities.AppExecutors;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by rushabh on 01/11/17.
 */

public class FirebaseDataSource
        implements
        FirestoreAdapter.DataReceivedListener{

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
    private final FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private final MutableLiveData<FirebaseUser> mFirebaseUser;

    //Firestore instance variables
    private final FirebaseFirestore mFirestore;
    private Query mQuery;
    private DocumentReference mUserRef;
    private ListenerRegistration mUserRegistration;
    private FirestoreAdapter mFirestoreAdapter;

    private final MutableLiveData<ArrayList<DocumentSnapshot>> mFriendsData;
    private final MutableLiveData<ArrayList<DocumentSnapshot>> mNudgesData;


    private int fetchLimit = 20;

    private FirebaseDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;

        mFirebaseUser = new MutableLiveData<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        initAuthListener();

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();
        mFriendsData = new MutableLiveData<>();
        mNudgesData = new MutableLiveData<>();
        initFirebaseAdapter();
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

                mExecutors.networkIO().execute(() -> {
                    mFirebaseUser.postValue(firebaseAuth.getCurrentUser());
                    initUserReference(firebaseAuth.getCurrentUser());
                    startFetchFriendsService();
                });

            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void initUserReference(FirebaseUser user){
        Log.d(LOG_TAG, "Initializing User Reference ");
        if (user != null) {
            mUserRef = mFirestore.collection("users").document(user.getUid());

            mUserRegistration = mUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

                    if (e != null) {
                        Log.w(LOG_TAG, "user:onEvent", e);
                        return;
                    }

                    if (snapshot.exists() && snapshot != null ) {
                            UserClass user = snapshot.toObject(UserClass.class);
                            Log.d(LOG_TAG, "Fetching data for: " + user.getUserName() + ", id: " + user.getUserIdentifier());
                            checkFCMtoken(user);

                        } else {
                            Log.d(LOG_TAG, " User reference is null");
                        }

                    }
                });
        }
    }

    private void initFirebaseAdapter(){
        Log.d(LOG_TAG, "Initializing Firebase Adapter ");
        mQuery = null;
        mFirestoreAdapter = new FirestoreAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.w(LOG_TAG, "initFirebaseAdapter: Error starting Firestoreadapter");
            }
        };

    }

    public LiveData<FirebaseUser> getFirebaseUser(){
        return mFirebaseUser;
    }

    public void startSignOut(){
        mFirebaseAuth.signOut();
    }

    public LiveData<ArrayList<DocumentSnapshot>> getFriendsViaQuery(){
        return mFriendsData;
    }

    /**
     * Starts an intent service to fetch the weather.
     */
    public void startFetchFriendsService() {
        Intent intentToFetch = new Intent(mContext, FirebaseSyncIntentService.class);
        mContext.startService(intentToFetch);
        Log.d(LOG_TAG, "Fetch Friends service created");
    }


    /**
     * Gets the newest weather
     */
    void fetchfriends() {
        Log.d(LOG_TAG, "Fetch friends started");
        mExecutors.networkIO().execute(() -> {
            try {


                if (mFirestoreAdapter != null) {

                    if(mUserRef!=null) {
                        //Fetch more data
                        if (mFirestoreAdapter.getItemCount() != 0) {
                            DocumentSnapshot snapshot = mFirestoreAdapter.getSnapshot(mFirestoreAdapter.getItemCount() - 1);

                            mQuery = mUserRef
                                    .collection("whatsapp_friends")
                                    .orderBy("timesContacted", Query.Direction.DESCENDING)
                                    .limit(fetchLimit)
                                    .startAfter(snapshot);

                        } else {

                            mQuery = mUserRef
                                    .collection("whatsapp_friends")
                                    .orderBy("timesContacted", Query.Direction.DESCENDING)
                                    .limit(fetchLimit);

                        }
                    } else Log.d(LOG_TAG, "fetchFriends, user reference is null ");

                    Log.d(LOG_TAG, "Fetch Friends, setting query");
                    mFirestoreAdapter.setQuery(mQuery);
                }
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onDataChanged() {

        mExecutors.networkIO().execute(() -> {

            mFriendsData.postValue(mFirestoreAdapter.getAllSnapshots());
//            Log.d(LOG_TAG, "Number of items fetched: " + String.valueOf(mFirestoreAdapter.getItemCount()));
        });
    }

    public void changeStar(DocumentSnapshot snapshot, ContactsClass contact){

        DocumentReference friendRef = mUserRef.collection(contact.WHATSAPP_FRIENDS).document(snapshot.getId());
        changeStar(friendRef,contact);
    }

    private Task<Void> changeStar(final DocumentReference friendRef, final ContactsClass contact) {
        // Create reference for new rating, for use inside the transaction

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.update(friendRef, contact.STARRED, contact.getStarred());

                return null;
            }
        }).addOnSuccessListener(mExecutors.networkIO(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "Contact starred / unstarred");
            }
        }).addOnFailureListener(mExecutors.networkIO(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, "Contact star could not be updated", e);
            }
        });

    }


    /*Check if FCM token for user is set*/
    public void checkFCMtoken(UserClass user){

        String token_value = FirebaseInstanceId.getInstance().getToken();
        String token_key = mContext.getResources().getString(R.string.firebase_cloud_messaging_token);

        if(user.getUserFCMToken()==null || user.getUserFCMToken()!= token_value){
            /* Update user FCM token */
            sendRegistrationToServer(token_key,token_value);
        }

    }

    public void sendRegistrationToServer(String token_key, String token_value){
        if(mUserRef!=null) {
            sendRegistrationTokenToServer(mUserRef,token_key, token_value);
        }
        else {
            Log.d(LOG_TAG, "Could not set FCM token, user reference is null");
        }
    }

    private Task<Void> sendRegistrationTokenToServer(DocumentReference reference, String token_key, String token_value){


        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.update(reference,token_key , token_value);

                return null;
            }
        }).addOnSuccessListener(mExecutors.networkIO(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "FCM token updated");
            }
        }).addOnFailureListener(mExecutors.networkIO(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, "Failed to update FCM Token for user", e);
            }
        });

    }

    public LiveData<ArrayList<DocumentSnapshot>> getNudges(){
        return mNudgesData;
    }




}
