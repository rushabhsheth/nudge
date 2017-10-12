package com.nudge.nudge.ActionFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nudge.nudge.R;

/**
 * Created by rushabh on 11/10/17.
 */

public class ActionButtonsFragment extends Fragment {

    private static final String TAG = "ActionButtons";

    public ActionButtonsFragment() {
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actionbuttons, container, false);

        return rootView;
    }



}
