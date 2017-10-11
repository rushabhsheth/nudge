package com.nudge.nudge.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nudge.nudge.R;

/**
 * Created by rushabh on 06/10/17.
 */

public class CalendarFragment extends Fragment {

    private static final String TAG = "CalendarFragment";

    public CalendarFragment(){
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
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        rootView.setTag(TAG);

        return rootView;
    }

}
