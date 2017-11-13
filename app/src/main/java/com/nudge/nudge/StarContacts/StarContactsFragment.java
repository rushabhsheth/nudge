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


import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.WriteBatch;
import com.nudge.nudge.Data.Database.ContactsReadPhone;
import com.nudge.nudge.Data.Database.ReferenceNames;
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.Data.Models.UserClass;
import com.nudge.nudge.Data.Network.FirestoreAdapter;
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
        ContactsReadPhone.ReturnLoadedDataListener,
        SearchActionClass.SearchQueryListener,
        StarContactsAdapter.onItemClickListener,
        EventListener<DocumentSnapshot>,
        FirestoreAdapter.DataReceivedListener{

    private static final String TAG = "StarContacts Fragment";

    @BindView(R.id.recyclerview_starcontacts)
    RecyclerView mRVstarcontacts;

    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<ContactsClass> mStarContactsData_allcontacts;
    private List<ContactsClass> mWhatsappContacts;

    private List<ContactsClass> mStarContactsData_favourites;
    private StarContactsAdapter mStarContactsAdapter;


    private Toolbar searchtoolbar;
    private SearchActionClass mSearchAction;

    private FirebaseFirestore mFirestore;
    private FirestoreAdapter mFirestoreAdapter;
    private FirebaseUser mUser;
    private StarActivityViewModel mViewModel;
    private DocumentReference mUserRef;
    private Query mQuery;
    private ListenerRegistration mUserRegistration;




    public StarContactsFragment() {
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mStarContactsData_allcontacts = new ArrayList<>();
        mWhatsappContacts = new ArrayList<>();
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
        if (mUser != null) {
            mUserRef = mFirestore.collection("users").document(mUser.getUid());

            ContactsClass contactsClass = new ContactsClass();
            // Get whatsapp friends
            mQuery = mUserRef
                    .collection("whatsapp_friends")
                    .orderBy(ReferenceNames.CONTACT_NAME, Query.Direction.ASCENDING);

            mFirestoreAdapter = new FirestoreAdapter(mQuery, this) {

                @Override
                protected void onError(FirebaseFirestoreException e) {
                    // Show a snackbar on errors
                    Toast.makeText(getContext(),
                            "FriendsFragment FirebaseAdapter Error: check logs for info.", Toast.LENGTH_LONG).show();
                }
            };
        }

        if (mUserRef != null) {
            mUserRegistration = mUserRef.addSnapshotListener(this);
        }
        if (mFirestoreAdapter != null) {
            mFirestoreAdapter.startListening();
        }

    }

    @Override
    public void onStop(){
        super.onStop();

        if(mFirestoreAdapter!=null){
            mFirestoreAdapter.stopListening();
        }
        if (mUserRegistration != null) {
            mUserRegistration.remove();
            mUserRegistration = null;
        }

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
//        int starPressed = starContact.getStarred();
//        DocumentReference friendRef = null;
//
//        for (int i = 0; i < mFirestoreAdapter.getItemCount(); i++) {
//            DocumentSnapshot snapshot = mFirestoreAdapter.getSnapshot(i);
//            ContactsClass contact = snapshot.toObject(ContactsClass.class);
//            if (contact.getContactId() == starContact.getContactId()) {
//                friendRef = mUserRef.collection(starContact.WHATSAPP_FRIENDS).document(snapshot.getId());
//                break;
//            }
//
//        }
//
//        if (starPressed == 0) {
//            item.getStarButton().setImageResource(R.drawable.ic_star_blue);
//            starContact.setStarred(1);
//
//            mStarContactsAdapter.addFavouriteItem(starContact);
//
//        } else {
//            item.getStarButton().setImageResource(R.drawable.ic_star_hollow);
//            starContact.setStarred(0);
//
//            //This is for database operations
//            mStarContactsAdapter.removeFavouriteItem(starContact, position);
//        }
//
//        if(friendRef!=null) {
//            changeStar(friendRef, starContact);
//        }

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


/*
        ContactsReadPhone function when data is loaded from contacts
*/
    public void returnLoadedData(List<ContactsClass> contactList) {
        mWhatsappContacts = contactList;
        Log.d(TAG, "Size of whatsapp contacts is "+ mWhatsappContacts.size());
//        firestoreUploadFriends();
//        mStarContactsAdapter.replaceAll(contactList);
//        mRVstarcontacts.scrollToPosition(0);
    }

    private void onUploadFriendsClicked() {
//        mStarContactsRead = new ContactsReadPhone(this, getContext(), getLoaderManager());
//        mStarContactsRead.loadContacts(0);
    }


    /**
     * Listener for the Restaurant document ({@link #mUserRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if(snapshot.exists()){
            UserClass user = snapshot.toObject(UserClass.class);
            Log.d(TAG, "Fetching data for: " + user.getUserName() + ", id: " + user.getUserEmail());
        } else {
            Log.d(TAG, " User reference is null");
        }


        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }
    }

    @Override
    public void onDataChanged() {

//        for(int i = 0; i< mFirestoreAdapter.getItemCount(); i++){
//            DocumentSnapshot snapshot = mFirestoreAdapter.getSnapshot(i);
//            ContactsClass contact = snapshot.toObject(ContactsClass.class);
//            mStarContactsData_allcontacts.add(contact);
//
//            if(contact.getStarred()==1){
//                mStarContactsAdapter.addFavouriteItem(contact);
//            }
//
//            //Updates whatsapp number if firestore doesnt have it
//            if(contact.getContactNumber()==null){
//                DocumentReference friendRef = mUserRef.collection(contact.WHATSAPP_FRIENDS).document(snapshot.getId());
//                for(int j=0;j<mWhatsappContacts.size();j++){
//                    ContactsClass mWhatsappContact = mWhatsappContacts.get(j);
//                    if(mWhatsappContact.getContactId()==contact.getContactId()) {
//                        addNumber(friendRef,mWhatsappContact);
//                    }
//                }
//
//            }
//        }
        Log.d(TAG, "Number of items fetched: " + String.valueOf(mFirestoreAdapter.getItemCount()));
    }

}
