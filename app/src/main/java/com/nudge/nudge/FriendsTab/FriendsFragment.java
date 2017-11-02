package com.nudge.nudge.FriendsTab;

import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.ProgressBar;
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
import com.nudge.nudge.MainActivityUtils.MainActivityViewModel;
import com.nudge.nudge.MainActivityUtils.MainViewModelFactory;
import com.nudge.nudge.R;
import com.nudge.nudge.Utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment
        extends Fragment
        implements
        FriendsCard.onClickListener,
        ItemRemovedListener,
        ActionButtonsFragment.onClickListener,
        MessageDialogFragment.SendListener {

    private static final String TAG = FriendsFragment.class.getSimpleName();

    @BindView(R.id.swipeView)
    SwipePlaceHolderView mSwipeView;

    @BindView(R.id.fragment_friendstab_actionbuttons)
    FrameLayout mActionButtonFrame;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private Context mContext;
    private FriendsFragmentViewModel mViewModel;

    private ActionButtonsFragment mActionButtons;

    private MessageDialogFragment mMessageDialog;


    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessageDialog = new MessageDialogFragment();
        mMessageDialog.setSendListener(this);

        //ViewModel
        mContext = this.getContext();
        FriendsFragmentViewModelFactory factory = InjectorUtils.provideFriendsFragmentViewModelFactory(mContext);
        mViewModel = ViewModelProviders.of(this, factory).get(FriendsFragmentViewModel.class);
        getFriendsData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        ButterKnife.bind(this, rootView);

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

        showLoading();
        return rootView;
    }

    private void getFriendsData() {

        mViewModel.getFriendsData().observe(this, listFriendsData -> {
            for (int i = 0; i < listFriendsData.size(); i++) {
                mSwipeView.addView(new FriendsCard(this, mContext, listFriendsData.get(i)));
            }
            if (listFriendsData != null && listFriendsData.size() != 0) showWeatherDataView();
            else showLoading();
        });
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     */
    private void showWeatherDataView() {
        // First, hide the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        // Finally, make sure the weather data is visible
        mSwipeView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     */
    private void showLoading() {
        // Then, hide the weather data
        mSwipeView.setVisibility(View.INVISIBLE);
        // Finally, show the loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (mFirestoreAdapter != null) {
//            mFirestoreAdapter.stopListening();
//        }
//        if (mUserRegistration != null) {
//            mUserRegistration.remove();
//            mUserRegistration = null;
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    //Interface method of StarContactsAdapter
    public void onStarClicked(ImageButton button, DocumentSnapshot snapshot, ContactsClass contact) {
        int starPressed = contact.getStarred();
        if (starPressed == 0) {
            button.setImageResource(R.drawable.ic_star_blue);
            contact.setStarred(1);
        } else {
            button.setImageResource(R.drawable.ic_star_hollow);
            contact.setStarred(0);
        }
        mViewModel.changeStar(snapshot,contact);

    }

    public void onFriendProfileClicked(ContactsClass contact) {
        startFriendProfileActivity();
    }

    //Swipe view recycler
    @Override
    public void onItemRemoved(int count) {
        if (count == 3) {
            loadNextDataFromFirestore(count);
        }
    }

    private void loadNextDataFromFirestore(int count) {
        mViewModel.requestMoreFriends();
    }


    //Action fragment click
    public void onMessageBtnClick() {
        List<Object> friendCards = mSwipeView.getAllResolvers();
        if(friendCards.size()!=0) {
            FriendsCard card = (FriendsCard) friendCards.get(0);
            String name = card.getProfile().getContactName();
            Log.d(TAG, "Contact name " + name);
            mMessageDialog.setMessageDialogText("Hi " + name.split(" ")[0] + "! How are you doing?");
            mMessageDialog.show(getActivity().getSupportFragmentManager(), MessageDialogFragment.TAG);
        }
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
