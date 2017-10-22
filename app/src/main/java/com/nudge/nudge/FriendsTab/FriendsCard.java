package com.nudge.nudge.FriendsTab;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rushabh on 07/10/17.
 */

@Layout(R.layout.friend_card_view)
public class FriendsCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.name_friend)
    private TextView nameAgeTxt;

    @View(R.id.friendcard_last_time_contacted)
    private TextView lastTimeContacted;

    @View(R.id.friendcard_times_contacted)
    private TextView timesContacted;

    @View(R.id.friend_card_view)
    public android.view.View view;

    @View(R.id.frame_friendtab_starcontact )
    private ViewGroup starViewGroup;


    private ContactsClass mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public FriendsCard(Context context, ContactsClass profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mProfile.getProfileImageUri()).into(profileImageView);
        nameAgeTxt.setText(mProfile.getContactName());
        lastTimeContacted.setText("Last time contacted: " + getNormalizedDate(mProfile.getLastTimeContacted()) + " days ago");
        timesContacted.setText("Times contacted: " + mProfile.getTimesContacted());
    }

    public String getNormalizedDate(long timeContacted){
        String normalizedTime = "";
        long timeNow = System.currentTimeMillis();
        long timeDiff = timeNow - timeContacted;
        int days =  (int) (timeDiff/(1000 * 60 * 60 *24));

        normalizedTime = String.valueOf(days);
        return normalizedTime;
    }

    @SwipeOut
    private void onSwipedOut(){
        //Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
//        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
//        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState(){
//        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
//        Log.d("EVENT", "onSwipeOutState");
    }


}
