package com.nudge.nudge.MainActivityUtils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.nudge.nudge.CameraFragment.CameraFragment;
import com.nudge.nudge.FreeTab.FreeFragment;
import com.nudge.nudge.FriendsTab.FriendsFragment;
import com.nudge.nudge.NudgesTab.NudgesFragment;
import com.nudge.nudge.R;
import com.nudge.nudge.UserProfile.UserProfileFragment;
import com.nudge.nudge.Utilities.InjectorUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
                        implements UserProfileFragment.onSignOutListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String KEY_ADAPTER_STATE = "com.nudge.nudge.KEY_ADAPTER_STATE";

    private static final int RC_SIGN_IN = 9001;
    private static final int RC_PROFILE = 123;

    @BindView(com.nudge.nudge.R.id.tabs)
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

    private ViewPagerAdapter mAdapter;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //ViewModel
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        setupViewPager(mNudgeViewPager);
        tabLayout.setupWithViewPager(mNudgeViewPager);
        setupTabIcons();

        createNotificationChannel();
        getNotificationClickIntent();
        checkSignIn();

    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT));
        }
    }

    private void getNotificationClickIntent(){

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(LOG_TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]
    }

    private void checkSignIn(){

        mViewModel.getFirebaseUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // User is signed in
                readContactsPersmission();
                Log.d(LOG_TAG, "Sign in successful for " + firebaseUser.getDisplayName());
            } else {
                startSignIn();
            }
        });
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);

    }

    private void setupViewPager(NudgeNonSwipableViewPager nudgeNonSwipableViewPager) {
        mAdapter.addFragment(new FriendsFragment(), "FRIENDS");
        mAdapter.addFragment(new NudgesFragment(), "NUDGES");
        mAdapter.addFragment(new CameraFragment(), "CAMERA");
        mAdapter.addFragment(new FreeFragment(), "FREE");
        mAdapter.addFragment(new UserProfileFragment(), "PROFILE");
        nudgeNonSwipableViewPager.setAdapter(mAdapter);
        nudgeNonSwipableViewPager.setCurrentItem(0);
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
            default:
                return super.onOptionsItemSelected(item);
        }
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
            mViewModel.setContactsPermissionGranted(true);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mViewModel.setContactsPermissionGranted(true);
            } else {
                mViewModel.setContactsPermissionGranted(false);
                readContactsPersmission();
                Toast.makeText(this, "App cannot work until you grant permission", Toast.LENGTH_LONG).show();
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
                .setIsSmartLockEnabled(!com.nudge.nudge.BuildConfig.DEBUG)
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
                mNudgeViewPager.setCurrentItem(0);
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                mViewModel.setIsSigningIn(false);
            } else {
                if (resultCode == RESULT_CANCELED) {
                    // Sign in was canceled by the user, finish the activity
                    Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                    mViewModel.setIsSigningIn(false);
                    finish();
                }
            }
        }

    }

    public void onSignOutClicked(){
        mViewModel.startSignOut();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}