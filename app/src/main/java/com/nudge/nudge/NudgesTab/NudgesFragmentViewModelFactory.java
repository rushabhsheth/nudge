package com.nudge.nudge.NudgesTab;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.nudge.nudge.Data.NudgeRepository;
import com.nudge.nudge.FriendsTab.FriendsFragmentViewModel;

/**
 * Created by rushabh on 07/11/17.
 */

public class NudgesFragmentViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final NudgeRepository mRepository;

    public NudgesFragmentViewModelFactory(NudgeRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new NudgesFragmentViewModel(mRepository);
    }
}