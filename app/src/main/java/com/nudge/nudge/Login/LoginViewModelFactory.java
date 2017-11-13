package com.nudge.nudge.Login;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.nudge.nudge.Data.NudgeRepository;
import com.nudge.nudge.FriendsTab.FriendsFragmentViewModel;

/**
 * Created by rushabh on 13/11/17.
 */

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final NudgeRepository mRepository;

    public LoginViewModelFactory(NudgeRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new LoginActivityViewModel(mRepository);
    }
}