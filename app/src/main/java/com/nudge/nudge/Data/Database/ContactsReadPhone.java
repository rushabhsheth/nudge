package com.nudge.nudge.Data.Database;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;


import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.Utilities.AppExecutors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 07/10/17.
 */

public class ContactsReadPhone implements
        Loader.OnLoadCompleteListener<Cursor> {

    private static final String TAG = ContactsReadPhone.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static ContactsReadPhone sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded weather forecasts
    private final AppExecutors mExecutors;


    private ContactsReadPhone(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
    }

    /**
     * Get the singleton for this class
     */
    public static ContactsReadPhone getInstance(Context context, AppExecutors executors) {
//        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new ContactsReadPhone(context.getApplicationContext(), executors);
//                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    List<ContactsClass> contactList;
    ReturnLoadedDataListener mCallback;
    List<ContactsClass> mWhatsappContacts;
    CursorLoader mCursorLoader;

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


    public void loadContacts(int id, ReturnLoadedDataListener listener) {
        mCallback = listener;
        contactList = new ArrayList<>();
        mWhatsappContacts = new ArrayList<>();
        mCursorLoader = initLoader(id);
        mCursorLoader.registerListener(id, this);
        mCursorLoader.startLoading();

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

    public CursorLoader initLoader(int id) {
        CursorLoader loader;
        /*Id =0 is for loading whatsapp contact ids*/
        if (id == 0) {

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
                    DISPLAY_NAME + " ASC");                  //Sort Order

        }
        return loader;
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch (id) {
            case 0:
                mExecutors.diskIO().execute(() -> {
                    if (cursor != null && cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {

                            ContactsClass singleWhatsappContact = getWhatsappContact(cursor);
                            mWhatsappContacts.add(singleWhatsappContact);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        mCallback.returnLoadedData(mWhatsappContacts);
                    }
                });
                break;

            default:
                mExecutors.diskIO().execute(() -> {
                    if (cursor != null && cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {

                            ContactsClass singleContact = getSingleContact(cursor);
                            contactList.add(singleContact);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        mCallback.returnLoadedData(contactList);
                    }
                });
                break;

        }
        onDestroy();
    }


    private ContactsClass getWhatsappContact(Cursor c) {
        ContactsClass whatsapp_contact = new ContactsClass();
        try {
            whatsapp_contact.setContactName(c.getString(c.getColumnIndexOrThrow(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)));
            whatsapp_contact.setContactId(c.getLong(c.getColumnIndexOrThrow(ContactsContract.RawContacts.CONTACT_ID)));
            whatsapp_contact.setTimesContacted(c.getInt(c.getColumnIndexOrThrow(ContactsContract.RawContacts.TIMES_CONTACTED)));
            whatsapp_contact.setLastTimeContacted(c.getLong(c.getColumnIndexOrThrow(ContactsContract.RawContacts.LAST_TIME_CONTACTED)));
            whatsapp_contact.setStarred(c.getInt(c.getColumnIndexOrThrow(ContactsContract.RawContacts.STARRED)));
            whatsapp_contact.setAccountType(c.getString(c.getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_TYPE)));

            final String[] PROJECTION = {
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            final String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";

            String whatsappContactId = c.getString(c.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
            final String[] selectionArgs = {
                    whatsappContactId
            };

            ContentResolver cr = mContext.getContentResolver();

            Cursor cursor = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION,
                    selection,
                    selectionArgs,
                    null);

            String normalized_number = null;
            String number = null;
            while (cursor.moveToNext()) {
//                normalized_number = cursor.getString(cursor.getColumnIndexOrThrow((ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)));
                number = cursor.getString(cursor.getColumnIndexOrThrow((ContactsContract.CommonDataKinds.Phone.NUMBER)));
                whatsapp_contact.setContactNumber(number);

            }
//            String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
//            Log.d(TAG, " Whatsapp name: "+ name + " , Whatsapp Number: " + number + " , Normalized number: " + normalized_number);
            cursor.close();


        } catch (IllegalArgumentException e) {
            Log.d(TAG, " No whatsapp id found from raw contacts");
        }
        return whatsapp_contact;
    }


    private ContactsClass getSingleContact(Cursor c) {
        ContactsClass mContactsClass = new ContactsClass();
        mContactsClass.setContactName(c.getString(c.getColumnIndexOrThrow(DISPLAY_NAME)));
        mContactsClass.setContactId(c.getLong(c.getColumnIndexOrThrow(_ID)));
        mContactsClass.setTimesContacted(c.getInt(c.getColumnIndexOrThrow(TIMES_CONTACTED)));
        mContactsClass.setLastTimeContacted(c.getLong(c.getColumnIndexOrThrow(LAST_TIME_CONTACTED)));
        mContactsClass.setStarred(c.getInt(c.getColumnIndexOrThrow(STARRED)));
        mContactsClass.setProfileImageUri(c.getString(c.getColumnIndexOrThrow(PHOTO_URI)));

        return mContactsClass;
    }

    public interface ReturnLoadedDataListener {
        void returnLoadedData(List<ContactsClass> contactList);
    }

    public void onDestroy() {

        // Stop the cursor loader
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }


}
