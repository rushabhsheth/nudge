package com.nudge.nudge.MainActivityUtils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.nudge.nudge.Data.NudgeRepository;

/**
 * Created by rushabh on 01/11/17.
 */

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final NudgeRepository mRepository;

    public MainViewModelFactory(NudgeRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}