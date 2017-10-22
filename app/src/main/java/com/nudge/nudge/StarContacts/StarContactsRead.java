package com.nudge.nudge.StarContacts;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;


import com.nudge.nudge.ContactsData.ContactsClass;

import java.util.ArrayList;
import java.util.List;

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
    List<ContactsClass> mWhatsappContacts;

    private static final Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    String _ID = ContactsContract.Contacts._ID;
    String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    String PHOTO_URI = ContactsContract.Contacts.PHOTO_URI;
    String LAST_TIME_CONTACTED = ContactsContract.Contacts.LAST_TIME_CONTACTED;
    String TIMES_CONTACTED = ContactsContract.Contacts.TIMES_CONTACTED;
    String STARRED = ContactsContract.Contacts.STARRED;

    private final String[] PROJECTION_COLUMNS = {
            _ID,
            DISPLAY_NAME,
            HAS_PHONE_NUMBER,
            PHOTO_URI,
            LAST_TIME_CONTACTED,
            TIMES_CONTACTED,
            STARRED
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

    public void loadContacts(int id){
        contactList = new ArrayList<>();
        mWhatsappContacts = new ArrayList<>();
        mLoaderManager.initLoader(id, null, this);

    }



    //Read whatsapp contacts
    Uri RAW_CONTENT_URI = ContactsContract.RawContacts.CONTENT_URI;

    String RAW_SELECTION_COLUMNS =
            ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?";

    String[] RAW_SELECTION_ARGS = {
            "com.whatsapp"
    };

    String[] RAW_PROJECTION_COLUMNS = {
            ContactsContract.RawContacts._ID,
            ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.RawContacts.TIMES_CONTACTED,
            ContactsContract.RawContacts.LAST_TIME_CONTACTED,
            ContactsContract.RawContacts.STARRED,
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.RawContacts.ACCOUNT_TYPE,
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader;
        /*Id =0 is for loading whatsapp contact ids*/
        if (id ==0){

            //Whatsapp contacts
            loader = new CursorLoader(
                    mContext,
                    RAW_CONTENT_URI,
                    RAW_PROJECTION_COLUMNS,
                    RAW_SELECTION_COLUMNS,
                    RAW_SELECTION_ARGS,
                    null);

        }
        /* Return all contacts from phone*/
        else {
            //All contacts
        loader = new CursorLoader(
                mContext,               //Context
                CONTENT_URI,            //URI
                PROJECTION_COLUMNS,                   //Projection
                null,                   //Selection
                null,                   //Selection Arguments
                DISPLAY_NAME+" ASC");                  //Sort Order

        }
        return  loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch (id) {
            case 0:
                if (cursor != null && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {

                        ContactsClass singleWhatsappContact = getWhatsappContact(cursor);
                        mWhatsappContacts.add(singleWhatsappContact);
                        cursor.moveToNext();
                    }
                    cursor.close();
                    mCallback.returnLoadedData(mWhatsappContacts);
                }
                break;

            default:
                if (cursor != null && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {

                        ContactsClass singleContact = getSingleContact(cursor);
                        contactList.add(singleContact);
                        cursor.moveToNext();
                    }
                    cursor.close();
                    mCallback.returnLoadedData(contactList);
                }
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ContactsClass getWhatsappContact(Cursor c){
        ContactsClass whatsapp_contact = new ContactsClass();
        try {
            whatsapp_contact.setContactName(c.getString(c.getColumnIndexOrThrow(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)));
            whatsapp_contact.setContactId(c.getLong(c.getColumnIndexOrThrow(ContactsContract.RawContacts.CONTACT_ID)));
            whatsapp_contact.setTimesContacted(c.getInt(c.getColumnIndexOrThrow(ContactsContract.RawContacts.TIMES_CONTACTED)));
            whatsapp_contact.setLastTimeContacted(c.getLong(c.getColumnIndexOrThrow(ContactsContract.RawContacts.LAST_TIME_CONTACTED)));
            whatsapp_contact.setStarred(c.getInt(c.getColumnIndexOrThrow(ContactsContract.RawContacts.STARRED)));
            whatsapp_contact.setAccountType(c.getString(c.getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_TYPE)));

        } catch (IllegalArgumentException e){
            Log.d(TAG, " No whatsapp id found from raw contacts");
        }
        return whatsapp_contact;
    }


    private ContactsClass getSingleContact (Cursor c){
        ContactsClass mContactsClass =  new ContactsClass();
        mContactsClass.setContactName(c.getString(c.getColumnIndexOrThrow(DISPLAY_NAME)));
        mContactsClass.setContactId(c.getLong(c.getColumnIndexOrThrow(_ID)));
        mContactsClass.setTimesContacted(c.getInt(c.getColumnIndexOrThrow(TIMES_CONTACTED)));
        mContactsClass.setLastTimeContacted(c.getLong(c.getColumnIndexOrThrow(LAST_TIME_CONTACTED)));
        mContactsClass.setStarred(c.getInt(c.getColumnIndexOrThrow(STARRED)));
        mContactsClass.setProfileImageUri(c.getString(c.getColumnIndexOrThrow(PHOTO_URI)));

        return mContactsClass;
    }

    public interface ReturnLoadedDataListener{
        void returnLoadedData(List<ContactsClass> contactList);
    }


}
