package com.nudge.nudge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nudge.nudge.FreeTab.FreeFragment;
import com.nudge.nudge.FriendsTab.FriendsFragment;
import com.nudge.nudge.NudgesTab.NudgesFragment;
import com.nudge.nudge.StarContacts.StarActivity;

import java.util.ArrayList;
import java.util.List;

import MainActivityUtils.NudgeNonSwipableViewPager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityUtils";

    private TabLayout tabLayout;
    private NudgeNonSwipableViewPager mNudgeViewPager;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNudgeViewPager = (NudgeNonSwipableViewPager) findViewById(R.id.nudge_viewpager);
        setupViewPager(mNudgeViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mNudgeViewPager);

        readContactsPersmission();

    }

    private void setupViewPager(NudgeNonSwipableViewPager nudgeNonSwipableViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NudgesFragment(), "NUDGES");
        adapter.addFragment(new FriendsFragment(), "FRIENDS");
        adapter.addFragment(new FreeFragment(), "FREE");
        nudgeNonSwipableViewPager.setAdapter(adapter);
        nudgeNonSwipableViewPager.setCurrentItem(1);
    }

   private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_profile):
                return true;
            case (R.id.action_star):
                startStarActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startStarActivity(){
        Intent intent = new Intent(this, StarActivity.class);
        startActivity(intent);
    }

    /**
     * Show the contacts in the ListView.
     */
    private void readContactsPersmission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            //Access granted and read contacts from phone
            // https://stackoverflow.com/questions/29915919/permission-denial-opening-provider-com-android-providers-contacts-contactsprovi

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
            } else {
                Toast.makeText(this, "Until you grant the permission, app cannot work", Toast.LENGTH_LONG).show();
            }
        }
    }

}
