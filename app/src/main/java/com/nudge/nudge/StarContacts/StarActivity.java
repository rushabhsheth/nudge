package com.nudge.nudge.StarContacts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.nudge.nudge.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rushabh on 05/10/17.
 */

public class StarActivity extends AppCompatActivity{

    private static final String TAG = "StarActivity";

    @BindView(R.id.toolbar_staractivity)
    Toolbar toolbar;


    private android.support.v7.widget.SearchView mSearchView;
    private SearchActionClass mSearchAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.activity_starcontacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_starcontacts, menu);

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

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_starcontacts);

        mSearchAction = ((StarContactsFragment) currentFragment).getSearchActionClass();
        mSearchView = mSearchAction.getSearchView();
        if (!mSearchView.isIconified()) {
            mSearchAction.actionClose();
        } else {
            super.onBackPressed();
        }
    }

}
