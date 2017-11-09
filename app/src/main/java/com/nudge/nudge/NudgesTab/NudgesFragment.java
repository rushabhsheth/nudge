package com.nudge.nudge.NudgesTab;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nudge.nudge.Data.Models.NudgeClass;
import com.nudge.nudge.R;
import com.nudge.nudge.Utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class NudgesFragment extends Fragment {

    private static final String TAG = "NudgesFragment";

    private Context mContext;

    @BindView(R.id.recyclerview_nudges)
    RecyclerView mRecyclerView;


    @BindView(R.id.pb_loading_indicator_nudges)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.view_empty_nudges)
    ViewGroup mEmptyView;

    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<NudgeClass> mNudgesData;
    private NudgesAdapter mNudgesAdapter;

    private NudgesFragmentViewModel mViewModel;


    public NudgesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNudgesData = new ArrayList<>();

        //ViewModel
        mContext = this.getContext();
        NudgesFragmentViewModelFactory factory = InjectorUtils.provideNudgesFragmentViewModelFactory(mContext);
        mViewModel = ViewModelProviders.of(this, factory).get(NudgesFragmentViewModel.class);
        getNudgesData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nudges, container, false);
        rootView.setTag(TAG);

        ButterKnife.bind(this, rootView);

        initRecyclerView();
//        loadSampleData();
        showEmpty();
        return rootView;
    }

    private void initRecyclerView(){
        int scrollPosition = 0;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        mNudgesAdapter = new NudgesAdapter(getContext(),mNudgesData);
        mRecyclerView.setAdapter(mNudgesAdapter);

//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(mRecyclerView);

    }

    private void loadSampleData(){
        mNudgesData = NudgesUtils.loadNudges(this.getContext());
        if(mNudgesData!=null) {
            for (int i = 0; i < mNudgesData.size(); i++) {
                mNudgesAdapter.addView(mNudgesData.get(i), i);
            }
        } else {
            Log.d(TAG,"Nudges data is null");
        }
    }

    private void getNudgesData() {

        mViewModel.getNudgesData().observe(this, listNudgesData -> {
            for (int i = 0; i < listNudgesData.size(); i++) {
                //TODO: add views for each nudge
            }

            if (listNudgesData != null && listNudgesData.size() != 0) showNudgesDataView();
            else showLoading();
        });
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     */
    private void showNudgesDataView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        // First, hide the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        // Finally, make sure the weather data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     */
    private void showLoading() {
        mEmptyView.setVisibility(View.INVISIBLE);
        // Then, hide the weather data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Finally, show the loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showEmpty(){
        mEmptyView.setVisibility(View.VISIBLE);
        // Then, hide the weather data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Finally, show the loading indicator
        mLoadingIndicator.setVisibility(View.GONE);
    }


}
