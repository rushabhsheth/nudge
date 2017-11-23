package com.nudge.nudge.Data.Network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nudge.nudge.Data.Database.ContactsReadPhone;
import com.nudge.nudge.Data.Database.ReferenceNames;
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.Data.Models.UserClass;
import com.nudge.nudge.R;
import com.nudge.nudge.Utilities.AppExecutors;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rushabh on 01/11/17.
 */

public class FirebaseDataSource
        implements
        FirestoreAdapter.DataReceivedListener,
        ContactsReadPhone.ReturnLoadedDataListener{

    private static final String LOG_TAG = FirebaseDataSource.class.getSimpleName();

    // Interval at which to sync contacts with the firestore. Use TimeUnit for convenience, rather than
    // writing out a bunch of multiplication ourselves and risk making a silly mistake.
    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String CONTACTS_SYNC_TAG = "contacts-sync";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static FirebaseDataSource sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded weather forecasts
    private final AppExecutors mExecutors;

    // Firestore tasks class instance
    private final FirestoreTasks mFirestoreTasks;

    //Firebase instance variables
    private final FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private final MutableLiveData<FirebaseUser> mFirebaseUserLiveData;

    //Firestore instance variables
    private final FirebaseFirestore mFirestore;
    private Query mQuery;
    private Query mNudgesQuery;
    private FirebaseUser mFirebaseUser;
    private DocumentReference mUserRef;
    private CollectionReference mUsers;
    private ListenerRegistration mUserRegistration;
    private FirestoreAdapter mFirestoreAdapter;
    private FirestoreAdapter mNudgeAdapter;
    private UserClass mUser;

    private final MutableLiveData<ArrayList<DocumentSnapshot>> mFriendsData;
    private final MutableLiveData<ArrayList<DocumentSnapshot>> mNudgesData;

    private ContactsReadPhone mContactsReadPhone;
    private List<ContactsClass> mWhatsappContacts;

    private int fetchLimit = 20;

    private FirebaseDataSource(Context context, AppExecutors executors, FirestoreTasks tasks) {
        mContext = context;
        mExecutors = executors;
        mFirestoreTasks = tasks;

        mFirebaseUserLiveData = new MutableLiveData<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        initAuthListener();

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();
        mFriendsData = new MutableLiveData<>();
        mNudgesData = new MutableLiveData<>();

        initFirebaseAdapter();
        initNudgeAdapter();
        initUsersCollectionReference();

        mWhatsappContacts = new ArrayList<>();
    }

    /**
     * Get the singleton for this class
     */
    public static FirebaseDataSource getInstance(Context context, AppExecutors executors, FirestoreTasks tasks) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirebaseDataSource(context.getApplicationContext(), executors, tasks);
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
                    mFirebaseUser = firebaseAuth.getCurrentUser();
                    mFirebaseUserLiveData.postValue(mFirebaseUser);
                    if (mFirebaseUser != null) {
                        initUserReference(mFirebaseUser);
                    }
                });

            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void initUserReference(FirebaseUser firebaseUser) {
        Log.d(LOG_TAG, "Initializing User Reference ");
        if (firebaseUser != null) {
            mUserRef = mFirestore.collection(ReferenceNames.USERS).document(firebaseUser.getUid());

            mUserRegistration = mUserRef.addSnapshotListener(mExecutors.networkIO(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

                    if (e != null) {
                        Log.w(LOG_TAG, "user:onEvent", e);
                        return;
                    }

                    if (snapshot.exists() && snapshot != null) {
                        mUser = snapshot.toObject(UserClass.class);
                        Log.d(LOG_TAG, "Fetching data for: " + mUser.getUserName() + ", id: " + mUser.getUserId());
                        checkFCMtoken(mUser);

                    } else {
                        Log.d(LOG_TAG, "User reference is null, creating new ");
                        mFirestoreTasks.createUserReference(mFirestore,mUserRef,firebaseUser);
                    }

                }
            });
        }
    }

    private void initFirebaseAdapter() {
        Log.d(LOG_TAG, "Initializing Firebase Adapter ");
        mQuery = null;
        mFirestoreAdapter = new FirestoreAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.w(LOG_TAG, "initFirebaseAdapter: Error starting Firestoreadapter");
            }
        };
    }

    private void initNudgeAdapter() {
        Log.d(LOG_TAG, "Initializing Nudge Adapter ");
        mNudgesQuery = null;
        mNudgeAdapter = new FirestoreAdapter(mQuery, new FirestoreAdapter.DataReceivedListener() {
            @Override
            public void onDataChanged() {
                mExecutors.networkIO().execute(() -> {
                    mNudgesData.postValue(mNudgeAdapter.getAllSnapshots());
                });
            }
        }) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.w(LOG_TAG, "initNudgeAdapter: Error starting Firestoreadapter");
            }
        };
    }


    private void initUsersCollectionReference(){
        mUsers = mFirestore.collection(ReferenceNames.USERS);
    }

    public LiveData<FirebaseUser> getFirebaseUser() {
        return mFirebaseUserLiveData;
    }

    public void startSignOut() {
        mFirebaseAuth.signOut();
    }

    public LiveData<ArrayList<DocumentSnapshot>> getFriendsViaQuery() {
        return mFriendsData;
    }

    /**
     * Starts an intent service to fetch the weather.
     */
    public void startFirestoreFetchService() {
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

                    if (mUserRef != null) {
                        //Fetch more data
                        if (mFirestoreAdapter.getItemCount() != 0) {
                            DocumentSnapshot snapshot = mFirestoreAdapter.getSnapshot(mFirestoreAdapter.getItemCount() - 1);

                            mQuery = mUserRef
                                    .collection(ReferenceNames.WHATSAPP_FRIENDS)
                                    .orderBy(ReferenceNames.TIMES_CONTACTED, Query.Direction.DESCENDING)
                                    .limit(fetchLimit)
                                    .startAfter(snapshot);

                        } else {

                            mQuery = mUserRef
                                    .collection(ReferenceNames.WHATSAPP_FRIENDS)
                                    .orderBy(ReferenceNames.TIMES_CONTACTED, Query.Direction.DESCENDING)
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

    /**
     * Gets the newest weather
     */
    void fetchNudges() {
        Log.d(LOG_TAG, "Fetch nudges started");
        mExecutors.networkIO().execute(() -> {
            try {

                if (mNudgeAdapter != null) {

                    if (mUserRef != null) {
                        //Fetch more data
                        if (mNudgeAdapter.getItemCount() != 0) {
                            DocumentSnapshot snapshot = mFirestoreAdapter.getSnapshot(mFirestoreAdapter.getItemCount() - 1);

                            mNudgesQuery = mUserRef
                                    .collection(ReferenceNames.NUDGES)
                                    .orderBy(ReferenceNames.TIMESTAMP, Query.Direction.DESCENDING)
                                    .limit(fetchLimit)
                                    .startAfter(snapshot);

                        } else {

                            mNudgesQuery = mUserRef
                                    .collection(ReferenceNames.NUDGES)
                                    .orderBy(ReferenceNames.TIMESTAMP, Query.Direction.DESCENDING)
                                    .limit(fetchLimit);

                        }
                    } else Log.d(LOG_TAG, "fetchNudges, user reference is null ");

                    Log.d(LOG_TAG, "Fetch Nudges, setting query");
                    mNudgeAdapter.setQuery(mNudgesQuery);
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
            checkFriendOnNudge(mFirestoreAdapter.getAllSnapshots());
//            Log.d(LOG_TAG, "Number of items fetched: " + String.valueOf(mFirestoreAdapter.getItemCount()));
        });
    }

    public void changeStar(DocumentSnapshot snapshot, ContactsClass contact) {

        DocumentReference friendRef = mUserRef.collection(ReferenceNames.WHATSAPP_FRIENDS).document(snapshot.getId());
        mFirestoreTasks.changeStar(mFirestore, friendRef, contact);
    }


    /*Check if FCM token for user is set*/
    public void checkFCMtoken(UserClass user) {

        String token_value = FirebaseInstanceId.getInstance().getToken();
        String token_key = mContext.getResources().getString(R.string.firebase_cloud_messaging_token);

        if (user.getFcmtoken() == null || user.getFcmtoken() != token_value) {
            /* Update user FCM token */
            sendRegistrationToServer(token_key, token_value);
        }

    }

    public void sendRegistrationToServer(String token_key, String token_value) {
        if (mUserRef != null) {
            mFirestoreTasks.sendRegistrationTokenToServer(mFirestore, mUserRef, token_key, token_value);
        } else {
            Log.d(LOG_TAG, "Could not set FCM token, user reference is null");
        }
    }


    public LiveData<ArrayList<DocumentSnapshot>> getNudges() {
        return mNudgesData;
    }


    /**
     * Schedules a repeating job service which fetches the weather.
     */
    public void scheduleRecurringContactsSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync Sunshine
        Job syncContactsJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync contacts's data */
                .setService(FirebaseContanctsSyncJobService.class)
                .setTag(CONTACTS_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncContactsJob);
        Log.d(LOG_TAG, "Contacts sync job scheduled");
    }

    public void syncContacts() {
        mContactsReadPhone = ContactsReadPhone.getInstance(mContext, mExecutors);
        mContactsReadPhone.loadContacts(0, this); //Whatsapp contacts

    }

    /*
        ContactsReadPhone function when data is loaded from contacts
*/
    public void returnLoadedData(List<ContactsClass> contactList) {
        mWhatsappContacts = contactList;
        Log.d(LOG_TAG, "Size of whatsapp contacts is " + mWhatsappContacts.size() + " mUser number of whatsapp friends: " + mUser.getNumberWhatsappFriends());

        /* Do this when starting app or once per day*/
        if(mUser.getNumberWhatsappFriends() == 0) {
           mFirestoreTasks.syncContacts(mFirestore, mUserRef, mWhatsappContacts);
        }
    }

    private void checkFriendOnNudge(ArrayList<DocumentSnapshot> mSnapshots){
        for(DocumentSnapshot snapshot: mSnapshots) {
            mFirestoreTasks.findFriendsOnNudge(mFirestore, mUserRef, mUsers, snapshot);
        }
    }

    public void nudgeFriend(DocumentSnapshot snapshot){

        mFirestoreTasks.nudgeFriend(mFirestore, mUserRef, mUser, snapshot);
    }

}
