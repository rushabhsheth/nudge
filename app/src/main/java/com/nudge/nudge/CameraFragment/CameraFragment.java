package com.nudge.nudge.CameraFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nudge.nudge.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rushabh on 06/10/17.
 */

public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    public CameraFragment(){
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
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        rootView.setTag(TAG);

        return rootView;

    }


}
