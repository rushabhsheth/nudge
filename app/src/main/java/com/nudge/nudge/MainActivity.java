package com.nudge.nudge;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nudge.nudge.Calendar.CalendarActivity;
import com.nudge.nudge.CameraFragment.CameraFragment;
import com.nudge.nudge.FreeTab.FreeFragment;
import com.nudge.nudge.FriendProfile.FriendActivity;
import com.nudge.nudge.FriendsTab.FriendsFragment;
import com.nudge.nudge.MainActivityUtils.MainActivityViewModel;
import com.nudge.nudge.NudgesTab.NudgesFragment;
import com.nudge.nudge.StarContacts.StarActivity;
import com.nudge.nudge.UserProfile.ProfileActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.nudge.nudge.MainActivityUtils.NudgeNonSwipableViewPager;
import com.nudge.nudge.UserProfile.UserProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    private static final int RC_SIGN_IN = 9001;
    private static final int RC_PROFILE = 123;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;


    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_nudge_plain_white,
            R.drawable.ic_camera,
            R.drawable.ic_free,
            R.drawable.ic_profile
    };

    @BindView(R.id.nudge_viewpager)
    NudgeNonSwipableViewPager mNudgeViewPager;

    private MainActivityViewModel mViewModel;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
//        setSupportActionBar(mToolbar);

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        setupViewPager(mNudgeViewPager);
        tabLayout.setupWithViewPager(mNudgeViewPager);
        setupTabIcons();

        mFirebaseAuth = FirebaseAuth.getInstance();
        initAuthListener();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        readContactsPersmission();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);

    }

    private void setupViewPager(NudgeNonSwipableViewPager nudgeNonSwipableViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsFragment(), "FRIENDS");
        adapter.addFragment(new NudgesFragment(), "NUDGES");
        adapter.addFragment(new CameraFragment(), "CAMERA");
        adapter.addFragment(new FreeFragment(), "FREE");
        adapter.addFragment(new UserProfileFragment(), "PROFILE");
        nudgeNonSwipableViewPager.setAdapter(adapter);
        nudgeNonSwipableViewPager.setCurrentItem(0);
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

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
//            case (R.id.action_userprofile):
//                startProfileActivity();
//                return true;
//            case (R.id.action_star):
//                startStarActivity();
//                return true;
//            case (R.id.action_calendar):
//                startCalendarActivity();
//                return true;
//            case (R.id.action_friendprofile):
//                startFriendProfileActivity();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startStarActivity() {
        Intent intent = new Intent(this, StarActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivityForResult(intent, RC_PROFILE);
    }

    private void startCalendarActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }


//    private void startFriendProfileActivity() {
//        Intent intent = new Intent(this, FriendActivity.class);
//        startActivity(intent);
//    }

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


    private void startSignIn() {

        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                .setPermissions(Arrays.asList("public_profile", "user_friends", "email"))
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                facebookIdp,
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        );

        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setProviders(providers)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
//                mUser = mFirebaseAuth.getCurrentUser();
                mViewModel.setIsSigningIn(false);
                mViewModel.setUser(mUser);

            } else {
                if (resultCode == RESULT_CANCELED) {
                    // Sign in was canceled by the user, finish the activity
                    Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                    mViewModel.setIsSigningIn(false);
                    finish();
                }
            }
        }

        else if (requestCode == RC_PROFILE ) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("SignOut")) {
                    String mSignOut = data.getExtras().getString("SignOut");
//                    Log.d(TAG, "Intent with RC_Profile. Sign Out? " + mSignOut);
                    if (mUser != null) {
                        mFirebaseAuth.signOut();
                        Toast.makeText(this, "Signed out", Toast.LENGTH_LONG).show();
                        mViewModel.setUser(null);
                        startSignIn();
                    }
                }
            }
        }

    }

    private void initAuthListener() {
        //Firebase Auth State Listener

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                mViewModel.setUser(mUser);

                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "Sign in successful, setting up view pager ");
                   } else {
                    startSignIn();
                }

            }
        };
    }


}