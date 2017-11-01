package com.nudge.nudge.Data.Database;

/**
 * Created by rushabh on 01/11/17.
 */

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * {@link Dao} which provides an api for all data operations with the {@link ContactDatabase}
 */
@Dao
public interface ContactDao {

    /**
     * Gets the contact for a single contactId
     *
     * @return {@link LiveData} with contact information for a single contact
     */
    @Query("SELECT * FROM contacts")
    LiveData<ContactEntry> getAllContacts ();

    /**
     * Inserts a list of {@link ContactEntry} into the contacts table. If there is a conflicting _id
     * or contact id the contact entry uses the {@link OnConflictStrategy} of replacing the contact.
     * The required uniqueness of these values is defined in the {@link ContactEntry}.
     *
     * @param contact A list of contact forecasts to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(ContactEntry... contact);



}
