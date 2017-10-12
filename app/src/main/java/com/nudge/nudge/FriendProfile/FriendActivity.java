package com.nudge.nudge.FriendProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import com.nudge.nudge.ActionFragments.ActionButtonsFragment;
import com.nudge.nudge.R;

/**
 * Created by rushabh on 05/10/17.
 */

public class FriendActivity extends AppCompatActivity {

    private static final String TAG = "FriendActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendprofile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friendprofile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.activity_friendprofile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

//    private void addActionButtonsFragment(){
//        Fragment fragment = new ActionButtonsFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_friendprofile, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

}
