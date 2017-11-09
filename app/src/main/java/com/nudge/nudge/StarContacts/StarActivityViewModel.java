package com.nudge.nudge.StarContacts;

import android.arch.lifecycle.ViewModel;

import com.nudge.nudge.Data.Models.ContactsClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 20/10/17.
 */

public class StarActivityViewModel extends ViewModel {

    private List<ContactsClass> mAllContacts;

    public StarActivityViewModel() {
        mAllContacts = new ArrayList<>();
    }

    public List<ContactsClass> getAllContacts() {
        return mAllContacts;
    }

    public void setAllContacts (List<ContactsClass> contactList) {
        this.mAllContacts = contactList;
    }

}
