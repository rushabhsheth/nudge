package com.nudge.nudge.Data.Network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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
        // Create reference for new rating, for use inside the transaction

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


}
