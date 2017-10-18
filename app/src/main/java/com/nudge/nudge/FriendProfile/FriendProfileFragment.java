package com.nudge.nudge.FriendProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nudge.nudge.R;
import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.StarContacts.StarContactsRead;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by rushabh on 06/10/17.
 */

public class FriendProfileFragment extends Fragment {

    private static final String TAG = "FriendProfileFragment";

    private RecyclerView mRecyclerView;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private ContactsClass mFriendData;
    private FriendProfileAdapter mFriendProfileAdapter;

    LinearLayout sliderDotsPanel;
    private int dotsCount;
    private ImageView[] dots;

    public FriendProfileFragment(){
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFriend();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friendprofile, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_friendprofile);

        int scrollPosition = 0;
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        mFriendProfileAdapter = new FriendProfileAdapter(getContext(),mFriendData);
        mRecyclerView.setAdapter(mFriendProfileAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        View actionButtons = rootView.findViewById(R.id.fragment_actionbuttons);
        actionButtons.bringToFront();

        addDots(rootView);
        addDotsScrollListener();

        return rootView;
    }

    //Add sample profile images to friends profile
    private void getFriend(){
        mFriendData = new ContactsClass();

        for(int i=1;i<=5;i++){
            int id = getResources().getIdentifier("pic"+String.valueOf(i), "drawable", getContext().getPackageName());
            String path = "android.resource://" + getContext().getPackageName() + "/" + String.valueOf(id);
//          Log.d(TAG,path);
            mFriendData.getProfileImageList().add(path);

        }

    }

    private void addDots(View rootView){
        //Add slider dots to the bottom
        sliderDotsPanel = (LinearLayout) rootView.findViewById(R.id.slider_dots);
        dotsCount = mFriendData.getProfileImageList().size();
        dots = new ImageView[dotsCount];

        for (int i =0; i<dotsCount; i++){

            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(10,0,10,0);

            sliderDotsPanel.addView(dots[i],params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.active_dot));

    }


    private void addDotsScrollListener(){
        //Recyclerview listener to scroll dots
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //mScrolling = false;
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    for (int i =0; i<dotsCount; i++){

                        dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.nonactive_dot));

                    }

                    int position = mLayoutManager.findFirstVisibleItemPosition();
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.active_dot));

                }
            }

        });

    }



}
