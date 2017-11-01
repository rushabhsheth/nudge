package com.nudge.nudge.Data.Database;

/**
 * Created by rushabh on 01/11/17.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

/**
 * {@link ContactDatabase} database for the application including a table for {@link ContactEntry}
 * with the DAO {@link ContactDao}.
 */

// List of the entry classes and associated TypeConverters
@Database(entities = {ContactEntry.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class ContactDatabase extends RoomDatabase {

    private static final String LOG_TAG = ContactDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "contacts";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static ContactDatabase sInstance;

    public static ContactDatabase getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ContactDatabase.class, ContactDatabase.DATABASE_NAME).build();
                Log.d(LOG_TAG, "Made new database");
            }
        }
        return sInstance;
    }

    // The associated DAOs for the database
    public abstract ContactDao contactDao();
}