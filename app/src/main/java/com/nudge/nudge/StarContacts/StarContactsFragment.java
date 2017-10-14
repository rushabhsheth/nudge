package com.nudge.nudge.StarContacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class StarContactsFragment extends Fragment {

    private static final String TAG = "StarContacts";

    private RecyclerView mRVstarcontacts;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<ContactsClass> mStarContactsData_allcontacts;
    private List<ContactsClass> mStarContactsData_favourites;
    private StarContactsAdapter mStarContactsAdapter;

    private StarContactsRead mStarContactsRead;


    public StarContactsFragment(){
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStarContactsData_allcontacts = new ArrayList<>();
        mStarContactsData_favourites = new ArrayList<>();
        getContacts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_starcontacts, container, false);
        rootView.setTag(TAG);

        //Initiatlize recycler view for all contacts

        mRVstarcontacts = (RecyclerView) rootView.findViewById(R.id.recyclerview_starcontacts);

        int scrollPosition = 0;
        mLayoutManager = new LinearLayoutManager(getActivity()); //Layout is reversed
        mRVstarcontacts.setLayoutManager(mLayoutManager);
        mRVstarcontacts.scrollToPosition(scrollPosition);

        mStarContactsAdapter = new StarContactsAdapter(getContext(),mStarContactsData_allcontacts,mStarContactsData_favourites);
        mRVstarcontacts.setAdapter(mStarContactsAdapter);

        return rootView;
    }

    private void getContacts(){
        mStarContactsRead = new StarContactsRead(getContext(),getLoaderManager());
        mStarContactsData_allcontacts = mStarContactsRead.loadContacts();


    }
}
