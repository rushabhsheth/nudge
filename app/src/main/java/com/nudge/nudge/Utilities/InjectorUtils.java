package com.nudge.nudge.Utilities;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.nudge.nudge.Data.Database.ContactsReadPhone;
import com.nudge.nudge.Data.Network.FirebaseDataSource;
import com.nudge.nudge.Data.Network.FirestoreTasks;
import com.nudge.nudge.Data.NudgeRepository;
import com.nudge.nudge.FriendsTab.FriendsFragmentViewModelFactory;
import com.nudge.nudge.Login.LoginViewModelFactory;
import com.nudge.nudge.MainActivityUtils.MainViewModelFactory;
import com.nudge.nudge.NudgesTab.NudgesFragmentViewModel;
import com.nudge.nudge.NudgesTab.NudgesFragmentViewModelFactory;

/**
 * Created by rushabh on 01/11/17.
 */

public class InjectorUtils {

    public static NudgeRepository provideRepository(Context context) {
        AppExecutors executors = AppExecutors.getInstance();
        FirestoreTasks tasks = FirestoreTasks.getInstance(context.getApplicationContext(),executors);
        FirebaseDataSource firebaseDataSource =
                FirebaseDataSource.getInstance(context.getApplicationContext(), executors,tasks);
        return NudgeRepository.getInstance(firebaseDataSource, executors);
    }

    public static FirebaseDataSource provideFirebaseDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        FirestoreTasks tasks = FirestoreTasks.getInstance(context.getApplicationContext(),executors);
        return FirebaseDataSource.getInstance(context.getApplicationContext(), executors,tasks);
    }

    public static SharedPreferenceUtils provideSharedPreferences(Context context) {
        return SharedPreferenceUtils.getInstance(context);
    }


    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        NudgeRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

    public static LoginViewModelFactory provideLoginActivityViewModelFactory(Context context) {
        NudgeRepository repository = provideRepository(context.getApplicationContext());
        return new LoginViewModelFactory(repository);
    }

    public static FriendsFragmentViewModelFactory provideFriendsFragmentViewModelFactory(Context context) {
        NudgeRepository repository = provideRepository(context.getApplicationContext());
        return new FriendsFragmentViewModelFactory(repository);
    }


    public static NudgesFragmentViewModelFactory provideNudgesFragmentViewModelFactory(Context context) {
        NudgeRepository repository = provideRepository(context.getApplicationContext());
        return new NudgesFragmentViewModelFactory(repository);
    }


}
