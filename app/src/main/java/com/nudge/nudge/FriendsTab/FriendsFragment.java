package com.nudge.nudge.FriendsTab;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.firestore.DocumentSnapshot;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.nudge.nudge.ActionFragments.ActionButtonsFragment;
import com.nudge.nudge.ActionFragments.MessageDialogFragment;
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.FriendProfile.FriendActivity;
import com.nudge.nudge.R;
import com.nudge.nudge.Utilities.InjectorUtils;

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

    private SwipeDecor mSwipeDecor;

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
        setupSwipeView(rootView);

        showLoading();
        return rootView;
    }

    private void setupSwipeView(View rootView) {

        rootView.post(new Runnable() {
            @Override
            public void run() {
                mSwipeDecor = new SwipeDecor()
                        .setPaddingTop(20)
                        .setViewGravity(Gravity.TOP)
                        .setViewGravity(Gravity.CENTER_HORIZONTAL)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.nudge_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.nudge_swipe_out_msg_view)
                        .setViewHeight(getView().getHeight() - mActionButtonFrame.getHeight())
                        .setViewWidth(getView().getWidth());//height is ready

                mSwipeView.getBuilder()
                        .setDisplayViewCount(2)
                        .setIsUndoEnabled(false)
                        .setSwipeDecor(mSwipeDecor);
            }
        });

    }

    private void getFriendsData() {

        mViewModel.getFriendsData().observe(this, listFriendsData -> {
            for (int i = 0; i < listFriendsData.size(); i++) {
                mSwipeView.addView(new FriendsCard(this, mContext, listFriendsData.get(i)));
            }
            setNudgeBtnEnabled();
            setMessageBtnEnabled();
            if (listFriendsData != null && listFriendsData.size() != 0) showFriendsDataView();
            else showLoading();
        });
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     */
    private void showFriendsDataView() {
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
//            mActionButtons.setSwipePlaceHolderView(mSwipeView);
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
        setNudgeBtnEnabled();
        setMessageBtnEnabled();
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

        if(number!=null) {
            Log.d(TAG, "Contact name: " + card.getProfile().getContactName() + " , Contact number: " + number);

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


    public void onNudgeBtnClick() {

        /*TODO: Send Nudge data to firestore*/
        List<Object> friendCards = mSwipeView.getAllResolvers();
        if (friendCards.size() != 0) {
            FriendsCard card = (FriendsCard) friendCards.get(0);

            //Remove the card while showing nudge
            mSwipeView.doSwipe(true);
        }

    }

    public void onRejectBtnClick() {

        if (mSwipeView != null) {
            mSwipeView.doSwipe(false);
        }
        /*TODO: Send Reject Data to firestore*/

    }

    public void onCardSwipedIn(){}

    public void onCardSwipedOut(){
        mSwipeView.deactivatePutBack();
    }

    private void setNudgeBtnEnabled() {
        List<Object> friendCards = mSwipeView.getAllResolvers();
        if (friendCards.size() != 0) {
            FriendsCard card = (FriendsCard) friendCards.get(0);
            ContactsClass contact = card.getProfile();
            mActionButtons.setNudgeBtnEnabled(contact.getOnNudge());
            if(!contact.getOnNudge()){
                mSwipeView.activatePutBack();
            }
        }
    }

    private void setMessageBtnEnabled() {
        List<Object> friendCards = mSwipeView.getAllResolvers();
        if (friendCards.size() != 0) {
            FriendsCard card = (FriendsCard) friendCards.get(0);
            ContactsClass contact = card.getProfile();
            if (contact.getContactNumber()==null) {
                mActionButtons.setMessageBtnEnable3d(false);
            } else {
                mActionButtons.setMessageBtnEnable3d(true);
            }
        }
    }

}
