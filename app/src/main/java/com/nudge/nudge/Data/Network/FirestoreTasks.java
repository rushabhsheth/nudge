package com.nudge.nudge.Data.Network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.nudge.nudge.Data.Database.ReferenceNames;
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.Data.Models.NudgeClass;
import com.nudge.nudge.Data.Models.UserClass;
import com.nudge.nudge.Utilities.AppExecutors;

import java.util.List;

/**
 * Created by rushabh on 09/11/17.
 */

public class FirestoreTasks {

    private static final String LOG_TAG = FirestoreTasks.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static FirestoreTasks sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded weather forecasts
    private final AppExecutors mExecutors;


    private FirestoreTasks(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
    }

    /**
     * Get the singleton for this class
     */
    public static FirestoreTasks getInstance(Context context, AppExecutors executors) {
//        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FirestoreTasks(context.getApplicationContext(), executors);
//                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }


    public Task<Void> sendRegistrationTokenToServer(FirebaseFirestore mFirestore, DocumentReference reference, String token_key, String token_value){


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


    public Task<Void> changeStar(FirebaseFirestore mFirestore, final DocumentReference friendRef, final ContactsClass contact) {
        // Create reference for new rating, for use inside the transaction

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.update(friendRef, ReferenceNames.STARRED, contact.getStarred());

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

    public void syncContacts(FirebaseFirestore mFirestore, DocumentReference mUserRef, List<ContactsClass> mWhatsappContacts) {

        int batch_size = 25;
        int total_items = mWhatsappContacts.size();
        int batches = (int) Math.ceil((double) mWhatsappContacts.size() / batch_size);

        for (int i = 0; i < batches; i++) {

            WriteBatch batch = mFirestore.batch();
            Log.d(LOG_TAG, " beginning batch write for contacts");

            for (int j = 0; j < batch_size; j++) {
                int counter = i * batch_size + j;

                if (counter < total_items) {
                    ContactsClass contact = mWhatsappContacts.get(i * batch_size + j);
                    batch.set(mUserRef.collection(ReferenceNames.WHATSAPP_FRIENDS).document(), contact);
                }
            }
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "Write batch succeeded.");
                    } else {
                        Log.w(LOG_TAG, "write batch failed.", task.getException());
                    }
                }
            });
        }
        mUserRef.update(ReferenceNames.NUMBER_WHATSAPP_FRIENDS,mWhatsappContacts.size());

    }

    private UserClass getUser(FirebaseUser user){
        UserClass mUser = new UserClass();
        mUser.setUserId(user.getUid());
        mUser.setUserName(user.getDisplayName());
        mUser.setUserEmail(user.getEmail());
        mUser.setUserPhone(user.getPhoneNumber());
        return mUser;
    }


    private Task<Void> addNumber(FirebaseFirestore mFirestore, final DocumentReference friendRef, final ContactsClass contact) {
        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.update(friendRef, ReferenceNames.CONTACT_NUMBER, contact.getContactNumber());

                return null;
            }
        }).addOnSuccessListener(mExecutors.networkIO(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "Contact number added");
            }
        }).addOnFailureListener(mExecutors.networkIO(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, "Contact number could not be updated", e);
            }
        });
    }

    /*
  * Find friends on nudge given their phone numbers
  * */
    public void findFriendsOnNudge(FirebaseFirestore mFirestore, DocumentReference mUserRef, CollectionReference mUsers, DocumentSnapshot snapshot) {
        Log.d(LOG_TAG, "Find friends on nudge started");
        mExecutors.diskIO().execute(() -> {
            try {
                ContactsClass contact = snapshot.toObject(ContactsClass.class);
                if (contact.getNudgeId() == null) {
                    mUsers.whereEqualTo(ReferenceNames.USER_PHONE, contact.getContactNumber())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
//                                            UserClass user = document.toObject(UserClass.class);
                                            DocumentReference friendRef = mUserRef.collection(ReferenceNames.WHATSAPP_FRIENDS).document(snapshot.getId());
                                            addOnNudge(mFirestore, friendRef, document);

                                        }
                                    } else {
                                        Log.d(LOG_TAG, "findFriendsOnNudge: Error finding friends ", task.getException());
                                    }
                                }
                            });
                }

            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }
        });
    }

    private Task<Void> addOnNudge(FirebaseFirestore mFirestore, final DocumentReference friendRef, DocumentSnapshot userDocument){
        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                // Commit to Firestore
                transaction.update(friendRef, ReferenceNames.NUDGE_ID, userDocument.getId());
                return null;
            }
        }).addOnSuccessListener(mExecutors.networkIO(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "On Nudge update to true");
            }
        }).addOnFailureListener(mExecutors.networkIO(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, "OnNudge could not be updated", e);
            }
        });

    }

    public void createUserReference(FirebaseFirestore mFirestore, DocumentReference mUserRef, FirebaseUser mUser) {
        UserClass user = getUser(mUser);
        mUserRef.set(user).addOnSuccessListener(mExecutors.networkIO(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "User Reference created for: " + user.getUserId());
                    }
                }).addOnFailureListener(mExecutors.networkIO(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, "Failed to create user reference for: " + user.getUserId(), e);
            }
        });
    }

    public Task<Void> nudgeFriend(FirebaseFirestore mFirestore, DocumentReference mUserRef, UserClass mUser,  DocumentSnapshot snapshot) {
        // Create reference for new rating, for use inside the transaction

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                NudgeClass nudge = getNudge(mUser,snapshot);
                // Commit to Firestore
                transaction.set(mUserRef.collection(ReferenceNames.NUDGES).document(),nudge);

                return null;
            }
        }).addOnSuccessListener(mExecutors.networkIO(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "Nudge added");
            }
        }).addOnFailureListener(mExecutors.networkIO(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_TAG, "Failed to add nudge to firestore", e);
            }
        });

    }

    private NudgeClass getNudge(UserClass mUser, DocumentSnapshot snapshot){
        NudgeClass nudge = new NudgeClass();
        ContactsClass friend = snapshot.toObject(ContactsClass.class);

        nudge.setSenderId(mUser.getUserId());
        nudge.setSenderName(mUser.getUserName());
        nudge.setSenderImageUrl(mUser.getUserImage());

        nudge.setReceiverId(friend.getNudgeId());
        nudge.setReceiverName(friend.getContactName());
        nudge.setSenderImageUrl(friend.getProfileImageUri());

        return nudge;
    }





}
