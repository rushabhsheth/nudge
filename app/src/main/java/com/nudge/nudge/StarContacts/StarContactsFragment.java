package com.nudge.nudge.StarContacts;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class StarContactsFragment extends Fragment  implements SearchView.OnQueryTextListener{

    private static final String TAG = "StarContacts";

    private RecyclerView mRVstarcontacts;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<ContactsClass> mStarContactsData_allcontacts;
    private List<ContactsClass> mStarContactsData_favourites;
    private StarContactsAdapter mStarContactsAdapter;

    private StarContactsRead mStarContactsRead;

    private SearchView searchView;
    private MenuItem searchMenuItem;

    public StarContactsFragment(){
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        mStarContactsAdapter = new StarContactsAdapter(getContext(),mStarContactsData_allcontacts,mStarContactsData_favourites,ALPHABETICAL_COMPARATOR);
        mRVstarcontacts.setAdapter(mStarContactsAdapter);

        mStarContactsAdapter.add(mStarContactsData_allcontacts);

        return rootView;
    }

    private void getContacts(){
        mStarContactsRead = new StarContactsRead(getContext(),getLoaderManager());
        mStarContactsData_allcontacts = mStarContactsRead.loadContacts();

        ContactsClass c = new ContactsClass();
        c.setContact_name("Archana");
        c.setId(123332);
        mStarContactsData_favourites.add(c);


    }

    private static final Comparator<ContactsClass> ALPHABETICAL_COMPARATOR = new Comparator<ContactsClass>() {
        @Override
        public int compare(ContactsClass a, ContactsClass b) {
            return a.getContact_name().compareTo(b.getContact_name());
        }
    };

    private static List<ContactsClass> filter(List<ContactsClass> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<ContactsClass> filteredModelList = new ArrayList<>();
        for (ContactsClass model : models) {
            final String text = model.getContact_name().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_starcontacts, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<ContactsClass> filteredModelList = filter(mStarContactsData_allcontacts, query);
        mStarContactsAdapter.replaceAll(filteredModelList);
        mRVstarcontacts.scrollToPosition(0);
        return true;
    }


}
