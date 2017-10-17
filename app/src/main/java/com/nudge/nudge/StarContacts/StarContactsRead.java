package com.nudge.nudge.StarContacts;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.nudge.nudge.ContactsData.ContactsClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rushabh on 07/10/17.
 */

public class StarContactsRead implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "StarContactsRead";

    private Context mContext;

    List<ContactsClass> contactList;
    LoaderManager mLoaderManager;
    ReturnLoadedDataListener mCallback;
    StarContactsFragment mFragement;

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



    StarContactsRead(StarContactsFragment fragment, Context context, LoaderManager loaderManager){
        this.mContext = context;
        this.mLoaderManager = loaderManager;
        this.mFragement = fragment;

        try {
            mCallback = (ReturnLoadedDataListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString()
                    + " must implement OnHeadlineSelectedListener");
        }


    }

    public void loadContacts(){
        contactList = new ArrayList<>();
        mLoaderManager.initLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                mContext,               //Context
                CONTENT_URI,            //URI
                PROJECTION_COLUMNS,                   //Projection
                null,                   //Selection
                null,                   //Selection Arguments
                DISPLAY_NAME+" ASC");                  //Sort Order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                ContactsClass singleContact = getSingleContact(cursor);
                contactList.add(singleContact);
                cursor.moveToNext();
            }
            cursor.close();
            mCallback.returnLoadedData(contactList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ContactsClass getSingleContact (Cursor c){
        ContactsClass mContactsClass =  new ContactsClass();
        mContactsClass.setContact_name(c.getString(c.getColumnIndexOrThrow(DISPLAY_NAME)));
        mContactsClass.setProfile_image_uri(c.getString(c.getColumnIndexOrThrow(PHOTO_URI)));

        Random r = new Random();
        int id = r.nextInt(1000000);
        mContactsClass.setId(id);

        return mContactsClass;
    }

    public interface ReturnLoadedDataListener{
        void returnLoadedData(List<ContactsClass> contactList);
    }

}
