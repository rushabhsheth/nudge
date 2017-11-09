package com.nudge.nudge.Data.Network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.nudge.nudge.Utilities.InjectorUtils;

/**
 * Created by rushabh on 02/11/17.
 * Immidiate sync called by {@link FirebaseDataSource} to fetch friends from firestore via query
 */

public class FirebaseSyncIntentService extends IntentService {
    private static final String LOG_TAG = FirebaseSyncIntentService.class.getSimpleName();

    public FirebaseSyncIntentService() {
        super("FirebaseSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Intent service started");
        FirebaseDataSource networkDataSource =
                InjectorUtils.provideFirebaseDataSource(this.getApplicationContext());
        networkDataSource.fetchfriends();
    }
}
