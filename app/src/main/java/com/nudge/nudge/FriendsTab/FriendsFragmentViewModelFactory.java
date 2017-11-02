package com.nudge.nudge.FriendsTab;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.nudge.nudge.Data.NudgeRepository;
import com.nudge.nudge.MainActivityUtils.MainActivityViewModel;

/**
 * Created by rushabh on 01/11/17.
 */

public class FriendsFragmentViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final NudgeRepository mRepository;

    public FriendsFragmentViewModelFactory(NudgeRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FriendsFragmentViewModel(mRepository);
    }
}