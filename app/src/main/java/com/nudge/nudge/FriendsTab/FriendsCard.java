package com.nudge.nudge.FriendsTab;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.R;

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
    public android.view.View cardView;

    @View(R.id.friendcard_starbutton )
    private ImageButton starViewButton;

    @View(R.id.friendcard_message_form)
    private EditText mMessageEditText;

    private ContactsClass mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private DocumentSnapshot documentSnapshot;


    private onClickListener mClickListener;


    public FriendsCard(onClickListener listner, Context context, DocumentSnapshot snapshot) {
        mContext = context;
        mProfile = snapshot.toObject(ContactsClass.class);
        mClickListener = listner;
        documentSnapshot = snapshot;

    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mProfile.getProfileImageUri()).into(profileImageView);
        nameAgeTxt.setText(mProfile.getContactName());
        lastTimeContacted.setText("Last time contacted: " + getNormalizedDate(mProfile.getLastTimeContacted()) + " days ago");
        timesContacted.setText("Times contacted: " + mProfile.getTimesContacted());

        if(mProfile.getStarred()==1) {
            starViewButton.setImageResource(R.drawable.ic_star_blue);
        }
        else {
            starViewButton.setImageResource(R.drawable.ic_star_hollow);
        }
        starViewButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mClickListener.onStarClicked(starViewButton, documentSnapshot, mProfile);
            }
        });

        cardView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mClickListener.onFriendProfileClicked(mProfile);
            }
        });

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
//        Log.d("EVENT", "onSwipedOut");
        mClickListener.onCardSwipedOut();
//        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
//        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
//        Log.d("EVENT", "onSwipedIn");
        mClickListener.onCardSwipedIn();
    }

    @SwipeInState
    private void onSwipeInState(){
//        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
//        Log.d("EVENT", "onSwipeOutState");
    }


    public interface onClickListener{
        void onStarClicked(ImageButton button, DocumentSnapshot snapshot, ContactsClass contact);
        void onFriendProfileClicked(ContactsClass contact);
        void onCardSwipedIn();
        void onCardSwipedOut();
    }

    public void setMessage(String message){
        mMessageEditText.setText(message);
    }

    public ContactsClass getProfile(){
        return this.mProfile;
    }

    public DocumentSnapshot getDocumentSnapshot(){
        return this.documentSnapshot;
    }
}
