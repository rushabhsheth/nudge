package com.nudge.nudge.StarContacts;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.firebase.ui.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.ContactsData.UserClass;
import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rushabh on 06/10/17.
 */

public class StarContactsFragment extends Fragment implements
        StarContactsRead.ReturnLoadedDataListener,
        SearchActionClass.SearchQueryListener,
        StarContactsAdapter.onItemClickListener{

    private static final String TAG = "StarContacts Fragment";

    @BindView(R.id.recyclerview_starcontacts)
    RecyclerView mRVstarcontacts;

    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<ContactsClass> mStarContactsData_allcontacts;
    private List<ContactsClass> mStarContactsData_favourites;
    private StarContactsAdapter mStarContactsAdapter;

    private StarContactsRead mStarContactsRead;

    private Toolbar searchtoolbar;
    private SearchActionClass mSearchAction;

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;
    private StarActivityViewModel mViewModel;

    DocumentReference mUserRef;

    public StarContactsFragment() {
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mStarContactsData_allcontacts = new ArrayList<>();
        mStarContactsData_favourites = new ArrayList<>();

        // View model
        mViewModel = ViewModelProviders.of(this).get(StarActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_starcontacts, container, false);
        rootView.setTag(TAG);

        ButterKnife.bind(this, rootView);

        initRecyclerView();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop(){
        super.onStop();

    }

    private void initRecyclerView(){
        //Initiatlize recycler view for all contacts
        int scrollPosition = 0;
        mLayoutManager = new LinearLayoutManager(getActivity()); //Layout is reversed
        mRVstarcontacts.setLayoutManager(mLayoutManager);
        mRVstarcontacts.scrollToPosition(scrollPosition);

        mStarContactsAdapter = new StarContactsAdapter(this, getContext(), mStarContactsData_favourites, ALPHABETICAL_COMPARATOR);
        mRVstarcontacts.setAdapter(mStarContactsAdapter);

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
            case (R.id.action_addfavourites):
                onUploadFriendsClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Interface method of StarContactsAdapter
    public void onItemClicked(StarContactsAdapter.VHItem item, ContactsClass starContact, int position) {
        int starPressed = starContact.getStarred();
        if (starPressed==0) {
            item.getStarButton().setImageResource(R.drawable.ic_star_blue);
            starContact.setStarred(1);

            mStarContactsAdapter.addFavouriteItem(starContact);

        } else {
            item.getStarButton().setImageResource(R.drawable.ic_star_hollow);
            starContact.setStarred(0);

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
            return a.getContactName().compareTo(b.getContactName());
        }
    };

    private static List<ContactsClass> filter(List<ContactsClass> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<ContactsClass> filteredModelList = new ArrayList<>();
        for (ContactsClass model : models) {
            final String text = model.getContactName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public SearchActionClass getSearchActionClass() {
        return mSearchAction;
    }

    private void showTodoToast() {
        Toast.makeText(getContext(), "TODO: Implement", Toast.LENGTH_SHORT).show();
    }


    //Interface implementation of method in @StarContactsRead
    public void returnLoadedData(List<ContactsClass> contactList) {
        mStarContactsData_allcontacts = contactList;
        Log.d(TAG, "Size of contacts is "+mStarContactsData_allcontacts.size());
//        mStarContactsAdapter.replaceAll(contactList);
//        mRVstarcontacts.scrollToPosition(0);
    }

    private void onUploadFriendsClicked() {

        int batch_size = 25;
        int total_items = mStarContactsData_allcontacts.size();
        int batches = (int) Math.ceil((double) mStarContactsData_allcontacts.size() / batch_size);

        for (int i = 0; i < batches; i++) {

            WriteBatch batch = mFirestore.batch();
            Log.d(TAG, " beginning batch write for contacts");
            UserClass user = getUser(mUser);
            Log.d(TAG, "FirebaseAuth user id: " + mUser.getEmail() + " , " + String.valueOf(mUser.getUid()));
            mUserRef = mFirestore.collection("users").document(user.getUserId());

            //Add user
            batch.set(mUserRef, user);

            for (int j = 0; j < batch_size; j++) {
                int counter = i * batch_size + j;

                if (counter < total_items) {
                    ContactsClass contact = mStarContactsData_allcontacts.get(i * batch_size + j);
                    batch.set(mUserRef.collection("whatsapp_friends").document(), contact);
                }
            }
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Write batch succeeded.");
                    } else {
                        Log.w(TAG, "write batch failed.", task.getException());
                    }
                }
            });
        }

    }

    private UserClass getUser(FirebaseUser user){
        UserClass mUser = new UserClass();
        mUser.setUserId(user.getUid());
        mUser.setUserIdentifier(user.getEmail());
        mUser.setUserName(user.getDisplayName());
        return mUser;
    }


}
