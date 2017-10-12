package com.nudge.nudge.UserProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nudge.nudge.R;

/**
 * Created by rushabh on 06/10/17.
 */

public class UserProfileFragment extends Fragment {

    private static final String TAG = "CalendarFragment";

    public UserProfileFragment(){
        //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_userprofile, container, false);
        rootView.setTag(TAG);

        return rootView;
    }

}
