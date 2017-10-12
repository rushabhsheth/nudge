package com.nudge.nudge.FriendProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nudge.nudge.R;
import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.StarContacts.StarContactsRead;

import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class FriendProfileFragment extends Fragment {

    private static final String TAG = "FriendProfileFragment";

    private RecyclerView mRecyclerView;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private ContactsClass mFriendData;
    private FriendProfileAdapter mFriendProfileAdapter;


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

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        return rootView;
    }

    private void getFriend(){
        mFriendData = new ContactsClass();


        for(int i=1;i<=5;i++){
            int id = getResources().getIdentifier("pic"+String.valueOf(i), "drawable", getContext().getPackageName());
            String path = "android.resource://" + getContext().getPackageName() + "/" + id;
            mFriendData.getProfileImageList().add(path);
        }


    }



}
