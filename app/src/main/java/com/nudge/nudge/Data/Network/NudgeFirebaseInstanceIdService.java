package com.nudge.nudge.Data.Network;

import android.app.Service;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nudge.nudge.R;
import com.nudge.nudge.Utilities.SharedPreferenceUtils;

/**
 * Created by rushabh on 03/11/17.
 */

public class NudgeFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = NudgeFirebaseInstanceIdService.class.getSimpleName();
    private static final String NUDGES_TOPIC = "nudges";

    private static final String NUDGE_SAVE_FCM_TOKEN = "nudge-save-fcm-token";

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        // All users are subscribed to nudges
        FirebaseMessaging.getInstance()
                .subscribeToTopic(NUDGES_TOPIC);

        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.firebase_cloud_messaging_token), token);

        Job myJob = dispatcher.newJobBuilder()
                .setService(SaveFCMTokenService.class)
                .setTag(NUDGE_SAVE_FCM_TOKEN)
                  /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                .setExtras(bundle)
                .build();

        dispatcher.schedule(myJob);
        Log.d(TAG, "Set / Replace FCM token Job scheduled");

    }

}
