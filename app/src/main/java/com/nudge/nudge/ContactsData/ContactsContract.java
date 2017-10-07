package com.nudge.nudge.ContactsData;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rushabh on 07/10/17.
 */

public class ContactsContract {

    public static final String CONTENT_AUTHORITY = "com.nudge.nudge";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTACTS = "contacts";


    /* Inner class that defines the table contents of the contacts table */
    public static final class ContactsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        public static final String TABLE_NAME = "contacts";


        public static Uri buildContactsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
