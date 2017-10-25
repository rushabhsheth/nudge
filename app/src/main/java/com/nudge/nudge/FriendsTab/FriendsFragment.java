package com.nudge.nudge.FriendsTab;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.User;
import com.google.android.gms.common.api.BooleanResult;
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
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.SwipeViewBinder;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.nudge.nudge.ActionFragments.ActionButtonsFragment;
import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.ContactsData.UserClass;
import com.nudge.nudge.FirebaseClasses.FirestoreAdapter;
import com.nudge.nudge.FriendProfile.FriendActivity;
import com.nudge.nudge.R;
import com.nudge.nudge.StarContacts.StarContactsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment
        extends Fragment
        implements
        EventListener<DocumentSnapshot>,
        FirestoreAdapter.DataReceivedListener,
        FriendsCard.onStarClickListener,
        ItemRemovedListener{

    private static final String TAG = "FriendsFragment";

    @BindView(R.id.swipeView)
    SwipePlaceHolderView mSwipeView;

    private Context mContext;
    private ActionButtonsFragment mActionButtons;

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private Query mQuery;
    private DocumentReference mUserRef;
    private ListenerRegistration mUserRegistration;

    private FirestoreAdapter mFirestoreAdapter;

    private int fetchLimit = 20;

    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        mUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        ButterKnife.bind(this, rootView);

        mContext = rootView.getContext();
        initActionButtons();

        mSwipeView.getBuilder()
                .setDisplayViewCount(2)
                .setIsUndoEnabled(false)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setViewGravity(Gravity.TOP)
                        .setViewGravity(Gravity.CENTER_HORIZONTAL)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.nudge_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.nudge_swipe_out_msg_view));

        mSwipeView.addItemRemoveListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume(){
        super.onResume();

        if (mUser != null) {
            mUserRef = mFirestore.collection("users").document(mUser.getUid());

            // Get whatsapp friends
            mQuery = mUserRef
                    .collection("whatsapp_friends")
                    .orderBy("timesContacted", Query.Direction.DESCENDING)
                    .limit(fetchLimit);

            mFirestoreAdapter = new FirestoreAdapter(mQuery, this){

                @Override
                protected void onError(FirebaseFirestoreException e) {
                    // Show a snackbar on errors
                    Toast.makeText(getContext(),
                            "FriendsFragment FirebaseAdapter Error: check logs for info.", Toast.LENGTH_LONG).show();
                }
            };


        }

        if (mUserRef != null) {
            mUserRegistration = mUserRef.addSnapshotListener(this);
        }
        if (mFirestoreAdapter != null) {
            mFirestoreAdapter.startListening();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mFirestoreAdapter!=null){
            mFirestoreAdapter.stopListening();
        }
        if (mUserRegistration != null) {
            mUserRegistration.remove();
            mUserRegistration = null;
        }
    }

    /**
     * Listener for the Restaurant document ({@link #mUserRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if(snapshot.exists()){
            UserClass user = snapshot.toObject(UserClass.class);
            Log.d(TAG, "Fetching data for: " + user.getUserName() + ", id: " + user.getUserIdentifier());
        } else {
            Log.d(TAG, " User reference is null");
        }


        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }
    }



    private void startFriendProfileActivity(){
        Intent intent = new Intent(getContext(), FriendActivity.class);
        startActivity(intent);
    }

    private void initActionButtons(){
        String tag_actionbuttons_friendtab = "tag_actionbuttons_friendtab";
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        mActionButtons = new ActionButtonsFragment();
        childFragTrans.add(R.id.fragment_friendstab_actionbuttons, mActionButtons,tag_actionbuttons_friendtab);
        childFragTrans.addToBackStack(null);
        childFragTrans.commit();

        if (mActionButtons != null) {
            mActionButtons.setSwipePlaceHolderView(mSwipeView);
        }

    }

    @Override
    public void onDataChanged() {

        for(int i = 0; i< mFirestoreAdapter.getItemCount(); i++){
//            ContactsClass contact  = mFirestoreAdapter.getSnapshot(i).toObject(ContactsClass.class);
            mSwipeView.addView(new FriendsCard(this, mContext, mFirestoreAdapter.getSnapshot(i), mSwipeView));
        }
        Log.d(TAG, "Number of items fetched: " + String.valueOf(mFirestoreAdapter.getItemCount()));
    }

    //Interface method of StarContactsAdapter
    public void onStarClicked(ImageButton button, DocumentSnapshot snapshot, ContactsClass contact) {
        int starPressed = contact.getStarred();
        DocumentReference friendRef = mUserRef.collection(contact.WHATSAPP_FRIENDS).document(snapshot.getId());

        if (starPressed==0) {
            button.setImageResource(R.drawable.ic_star_blue);
            contact.setStarred(1);

            changeStar(friendRef,contact);



        } else {
            button.setImageResource(R.drawable.ic_star_hollow);
            contact.setStarred(0);

            changeStar(friendRef,contact);
        }

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
        }) .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Contact starred / unstarred");
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Contact star could not be updated", e);
                    }
                });

    }

    //Swipe view recycler
    @Override
    public void onItemRemoved(int count){
//        Log.d(TAG, " Number of items in swipeview" + String.valueOf(count));
        if(count==2) {
            loadNextDataFromFirestore(count);
        }
    }

    private void loadNextDataFromFirestore(int count) {
        Log.d(TAG, "Number of snapshots in adapter: " + String.valueOf(mFirestoreAdapter.getItemCount()));
        DocumentSnapshot snapshot = mFirestoreAdapter.getSnapshot(mFirestoreAdapter.getItemCount() - 1);

        mQuery = mUserRef
                .collection("whatsapp_friends")
                .orderBy("timesContacted", Query.Direction.DESCENDING)
                .limit(fetchLimit)
                .startAfter(snapshot);

        mFirestoreAdapter.setQuery(mQuery);
    }




}
