package com.nudge.nudge.FriendsTab;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.CountDownTimer;
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

import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.SwipeViewBinder;
import com.nudge.nudge.ActionFragments.ActionButtonsFragment;
import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.ContactsData.UserClass;
import com.nudge.nudge.FirebaseClasses.FirestoreAdapter;
import com.nudge.nudge.FriendProfile.FriendActivity;
import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment
        extends Fragment
        implements EventListener<DocumentSnapshot>, FirestoreAdapter.DataReceivedListener{

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
        if(mUser==null){
            //TODO startSignIn
            Log.w(TAG, "User is null, start sign in process");
        }
        else {
            // Get reference to the user
            Log.d(TAG, "FirebaseAuth user id: " + String.valueOf(mUser.getUid()));
            mUserRef = mFirestore.collection("users").document(mUser.getUid());

            // Get whatsapp friends
            mQuery = mUserRef
                    .collection("whatsapp_friends")
                    .orderBy("timesContacted", Query.Direction.DESCENDING)
                    .limit(100);

            mFirestoreAdapter = new FirestoreAdapter(mQuery, this);
        }

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
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setViewGravity(Gravity.TOP)
                        .setViewGravity(Gravity.CENTER_HORIZONTAL)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.nudge_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.nudge_swipe_out_msg_view));



//        for(FriendsProfileClass profile : FriendsUtils.loadProfiles(this.getContext())){
//            mSwipeView.addView(new FriendsCard(mContext, profile, mSwipeView));
//        }


//        List<Object> resolverList = new ArrayList<>();
//        resolverList = mSwipeView.getAllResolvers();
//        for(int i = 0; i < 1;i++) {
//            FriendsCard friend = (FriendsCard) resolverList.get(i);
//            friend.view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startFriendProfileActivity();
//                }
//            });
//        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mUserRef != null) {
            mUserRegistration = mUserRef.addSnapshotListener(this);

        }
        if(mFirestoreAdapter!=null){
         mFirestoreAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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

    public void initActionButtons(){
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
            ContactsClass contact  = mFirestoreAdapter.getSnapshot(i).toObject(ContactsClass.class);
            mSwipeView.addView(new FriendsCard(mContext, contact, mSwipeView));
        }
        Log.d(TAG, "Number of items fetched: " + String.valueOf(mFirestoreAdapter.getItemCount()));
    }

}
