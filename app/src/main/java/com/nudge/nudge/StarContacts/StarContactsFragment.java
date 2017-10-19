package com.nudge.nudge.StarContacts;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class StarContactsFragment extends Fragment implements
        StarContactsRead.ReturnLoadedDataListener,
        SearchActionClass.SearchQueryListener,
        StarContactsAdapter.onItemClickListener {

    private static final String TAG = "StarContacts";

    private RecyclerView mRVstarcontacts;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<ContactsClass> mStarContactsData_allcontacts;
    private List<ContactsClass> mStarContactsData_favourites;
    private StarContactsAdapter mStarContactsAdapter;

    private StarContactsRead mStarContactsRead;

    private Toolbar searchtoolbar;
    private SearchActionClass mSearchAction;

    public StarContactsFragment() {
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mStarContactsData_allcontacts = new ArrayList<>();
        mStarContactsData_favourites = new ArrayList<>();

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

        mStarContactsAdapter = new StarContactsAdapter(this, getContext(), mStarContactsData_favourites, ALPHABETICAL_COMPARATOR);
        mRVstarcontacts.setAdapter(mStarContactsAdapter);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchtoolbar = (Toolbar) getActivity().findViewById(R.id.searchtoolbar);

        mSearchAction = new SearchActionClass(this, getContext(), searchtoolbar);
        mSearchAction.setSearchToolbar();

        mStarContactsRead = new StarContactsRead(this, getContext(), getLoaderManager());
        mStarContactsRead.loadContacts(0);

//        Log.d(TAG, String.valueOf(mStarContactsData_allcontacts.size()));

    }

    //Interface implementation of method in @StarContactsRead
    public void returnLoadedData(List<ContactsClass> contactList) {
        mStarContactsData_allcontacts = contactList;
        Log.d(TAG, "Size of contacts is "+mStarContactsData_allcontacts.size());
//        mStarContactsAdapter.replaceAll(contactList);
//        mRVstarcontacts.scrollToPosition(0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_starcontacts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_search):
                mSearchAction.actionSearch();
                mStarContactsAdapter.setIsSearch(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Interface method of StarContactsAdapter
    public void onItemClicked(StarContactsAdapter.VHItem item, ContactsClass starContact, int position) {
        boolean starPressed = starContact.getStarPressed();
        if (!starPressed) {
            item.getStarButton().setImageResource(R.drawable.ic_star_blue);
            starContact.setStarPressed(true);

            mStarContactsAdapter.addFavouriteItem(starContact);

        } else {
            item.getStarButton().setImageResource(R.drawable.ic_star_hollow);
            starContact.setStarPressed(false);

            //This is for database operations
            mStarContactsAdapter.removeFavouriteItem(starContact, position);

        }

    }


    //Interface method of SearchActionClass
    public void onSearchQuery(String query) {
//        Log.d(TAG, " Query: "+ query);
        final List<ContactsClass> filteredModelList = filter(mStarContactsData_allcontacts, query);
        mStarContactsAdapter.replaceAll(filteredModelList);
        mRVstarcontacts.scrollToPosition(0);
    }

    public void onSearchClose(){
//        Log.d(TAG, "onSearchClose: true");
        mStarContactsAdapter.setIsSearch(false);
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

    public SearchActionClass getSearchActionClass() {
        return mSearchAction;
    }


}
