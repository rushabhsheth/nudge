package com.nudge.nudge.StarContacts;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;


import com.nudge.nudge.FriendsTab.FriendsProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 07/10/17.
 */

public class StarContactsRead implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "StarContactsRead";

    private Context mContext;

    List<StarContactsClass> contactList;
    LoaderManager mLoaderManager;

    private static final Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    String _ID = ContactsContract.Contacts._ID;
    String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    String PHOTO_URI = ContactsContract.Contacts.PHOTO_URI;
    String LAST_TIME_CONTACTED = ContactsContract.Contacts.LAST_TIME_CONTACTED;
    String TIMES_CONTACTED = ContactsContract.Contacts.TIMES_CONTACTED;

    private final String[] PROJECTION_COLUMNS = {
            _ID,
            DISPLAY_NAME,
            HAS_PHONE_NUMBER,
            PHOTO_URI,
            LAST_TIME_CONTACTED,
            TIMES_CONTACTED
    };



    StarContactsRead(Context context, LoaderManager loaderManager){
        this.mContext = context;
        this.mLoaderManager = loaderManager;
    }

    public List<StarContactsClass> loadContacts(){
        contactList = new ArrayList<>();
        mLoaderManager.initLoader(0, null, this);
        return contactList;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                mContext,               //Context
                CONTENT_URI,            //URI
                PROJECTION_COLUMNS,                   //Projection
                null,                   //Selection
                null,                   //Selection Arguments
                null);                  //Sort Order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                StarContactsClass singleContact = getSingleContact(cursor);
                contactList.add(singleContact);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private StarContactsClass getSingleContact (Cursor c){
        StarContactsClass mStarContactsClass =  new StarContactsClass();
        mStarContactsClass.setContact_name(c.getString(c.getColumnIndexOrThrow(DISPLAY_NAME)));
        mStarContactsClass.setProfile_image_uri(c.getString(c.getColumnIndexOrThrow(PHOTO_URI)));

        return mStarContactsClass;
    }

}
