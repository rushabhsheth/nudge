package com.nudge.nudge.NudgesTab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class NudgesFragment extends Fragment {

    private static final String TAG = "NudgesFragment";

    private Context mContext;
    private RecyclerView mRecyclerView;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<NudgeClass> mNudgesData;
    private NudgesAdapter mNudgesAdapter;


    public NudgesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNudgesData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nudges, container, false);
        rootView.setTag(TAG);

        mContext = rootView.getContext();
        mRecyclerView = rootView.findViewById(R.id.recyclerview_nudges);

        int scrollPosition = 0;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        mNudgesAdapter = new NudgesAdapter(getContext(),mNudgesData);
        mRecyclerView.setAdapter(mNudgesAdapter);

//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(mRecyclerView);

        mNudgesData = NudgesUtils.loadNudges(this.getContext());
        for(int i = 0; i <mNudgesData.size();i++){
           mNudgesAdapter.addView(mNudgesData.get(i),i);

        }

        return rootView;
    }
}
