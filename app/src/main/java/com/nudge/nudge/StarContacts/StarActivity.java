package com.nudge.nudge.StarContacts;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.R;

import java.util.List;

/**
 * Created by rushabh on 05/10/17.
 */

public class StarActivity extends AppCompatActivity{

    private static final String TAG = "StarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_star);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.activity_starcontacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_starcontacts, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchMenuItem = menu.findItem(R.id.action_search);
//        searchView = (SearchView) searchMenuItem.getActionView();
//
//        searchView.setSearchableInfo(searchManager.
//                getSearchableInfo(getComponentName()));
//        searchView.setSubmitButtonEnabled(false);
//        searchView.setOnQueryTextListener(this);
//
//        return true;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
//            case (R.id.action_search):
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
