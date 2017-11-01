package com.nudge.nudge.FriendsTab;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.nudge.nudge.ActionFragments.ActionButtonsFragment;
import com.nudge.nudge.ActionFragments.MessageDialogFragment;
import com.nudge.nudge.Data.Database.ContactsClass;
import com.nudge.nudge.Data.Database.UserClass;
import com.nudge.nudge.Data.Network.FirestoreAdapter;
import com.nudge.nudge.FriendProfile.FriendActivity;
import com.nudge.nudge.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment
        extends Fragment
        implements
        EventListener<DocumentSnapshot>,
        FirestoreAdapter.DataReceivedListener,
        FriendsCard.onClickListener,
        ItemRemovedListener,
        ActionButtonsFragment.onClickListener,
        MessageDialogFragment.SendListener {

    private static final String TAG = "FriendsFragment";
    private static final String KEY_ADAPTER_STATE = "com.nudge.nudge.FriendsTab.KEY_ADAPTER_STATE";


    @BindView(R.id.swipeView)
    SwipePlaceHolderView mSwipeView;


    @BindView(R.id.fragment_friendstab_actionbuttons)
    FrameLayout mActionButtonFrame;

    private Context mContext;
    private ActionButtonsFragment mActionButtons;

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private Query mQuery;
    private DocumentReference mUserRef;
    private ListenerRegistration mUserRegistration;

    private FirestoreAdapter mFirestoreAdapter;

    private int fetchLimit = 20;

    private MessageDialogFragment mMessageDialog;


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

        mMessageDialog = new MessageDialogFragment();
        mMessageDialog.setSendListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        ButterKnife.bind(this, rootView);

        mContext = rootView.getContext();
        initActionButtons();

        mSwipeView.addItemRemoveListener(this);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                mSwipeView.getBuilder()
                        .setDisplayViewCount(2)
                        .setIsUndoEnabled(false)
                        .setSwipeDecor(new SwipeDecor()
                                .setPaddingTop(20)
                                .setViewGravity(Gravity.TOP)
                                .setViewGravity(Gravity.CENTER_HORIZONTAL)
                                .setRelativeScale(0.01f)
                                .setViewHeight(rootView.getHeight() - mActionButtonFrame.getHeight())
                                .setViewWidth(rootView.getWidth())//height is ready
                                .setSwipeInMsgLayoutId(R.layout.nudge_swipe_in_msg_view)
                                .setSwipeOutMsgLayoutId(R.layout.nudge_swipe_out_msg_view));
            }
        });

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
    public void onResume() {
        super.onResume();

        if (mUser != null) {
            mUserRef = mFirestore.collection("users").document(mUser.getUid());

            // Get whatsapp friends
            mQuery = mUserRef
                    .collection("whatsapp_friends")
                    .orderBy("timesContacted", Query.Direction.DESCENDING)
                    .limit(fetchLimit);

            mFirestoreAdapter = new FirestoreAdapter(mQuery, this) {

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
    public void onPause() {
        super.onPause();
        if (mFirestoreAdapter != null) {
            mFirestoreAdapter.stopListening();
        }
        if (mUserRegistration != null) {
            mUserRegistration.remove();
            mUserRegistration = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        TODO: Saved instance works for now from MainActivity > ViewPagerAdapter
//        if (savedInstanceState != null) {
//
//            myData = (List<String>) savedInstanceState.getSerializable("list");
//
//        } else {
//            if (myData != null) {
//                //returning from backstack, data is fine, do nothing
//            } else {
//                //newly created, compute data
//                myData = computeData();
//            }
//        }

    }


    /**
     * Listener for the Restaurant document ({@link #mUserRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (snapshot.exists()) {
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


    private void startFriendProfileActivity() {
        Intent intent = new Intent(getContext(), FriendActivity.class);
        startActivity(intent);
    }

    private void initActionButtons() {
        String tag_actionbuttons_friendtab = "tag_actionbuttons_friendtab";
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        mActionButtons = new ActionButtonsFragment();
        childFragTrans.add(R.id.fragment_friendstab_actionbuttons, mActionButtons, tag_actionbuttons_friendtab);
        childFragTrans.addToBackStack(null);
        childFragTrans.commit();

        if (mActionButtons != null) {
            mActionButtons.setSwipePlaceHolderView(mSwipeView);
            mActionButtons.setButtonClickListener(this);
        }

    }

    @Override
    public void onDataChanged() {

        for (int i = 0; i < mFirestoreAdapter.getItemCount(); i++) {

            ContactsClass contact  = mFirestoreAdapter.getSnapshot(i).toObject(ContactsClass.class);
            Log.d(TAG,contact.getContactName() + " " + String.valueOf(mFirestoreAdapter.getSnapshot(i).getId()));
            mSwipeView.addView(new FriendsCard(this, mContext, mFirestoreAdapter.getSnapshot(i)));
        }
        Log.d(TAG, "Number of items fetched: " + String.valueOf(mFirestoreAdapter.getItemCount()));
    }

    //Interface method of StarContactsAdapter
    public void onStarClicked(ImageButton button, DocumentSnapshot snapshot, ContactsClass contact) {
        int starPressed = contact.getStarred();
        DocumentReference friendRef = mUserRef.collection(contact.WHATSAPP_FRIENDS).document(snapshot.getId());

        if (starPressed == 0) {
            button.setImageResource(R.drawable.ic_star_blue);
            contact.setStarred(1);

            changeStar(friendRef, contact);


        } else {
            button.setImageResource(R.drawable.ic_star_hollow);
            contact.setStarred(0);

            changeStar(friendRef, contact);
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
        }).addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
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


    public void onFriendProfileClicked(ContactsClass contact) {
        startFriendProfileActivity();
    }

    //Swipe view recycler
    @Override
    public void onItemRemoved(int count) {
//        Log.d(TAG, " Number of items in swipeview" + String.valueOf(count));
        if (count == 3) {
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


    //Action fragment click
    public void onMessageBtnClick() {
        FriendsCard card = (FriendsCard) mSwipeView.getAllResolvers().get(0);
        String name = card.getProfile().getContactName();
        Log.d(TAG, "Contact name " + name);
        mMessageDialog.setMessageDialogText("Hi " + name.split(" ")[0] + "! How are you doing?");
        mMessageDialog.show(getActivity().getSupportFragmentManager(), MessageDialogFragment.TAG);
    }

    //Send message clicked in Message Dialog Fragment
    public void onSendClickedMessageDialog(String message) {

        FriendsCard card = (FriendsCard) mSwipeView.getAllResolvers().get(0);
        String number = card.getProfile().getContactNumber();
        Log.d(TAG, "Contact name: " + card.getProfile().getContactName() + " , Contact number: " + number) ;

        String toNumber = number.replace("+", "").replace(" ", "");
        String finalMessage = message + getString(R.string.nudge_dynamic_link);

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, finalMessage);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);


    }


}
