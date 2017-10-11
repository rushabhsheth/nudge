package com.nudge.nudge.FreeTab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nudge.nudge.FriendsTab.FriendsProfile;
import com.nudge.nudge.FriendsTab.FriendsUtils;
import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FreeFragment extends Fragment {

    private static final String TAG = "FreeFragment";

    private Context mContext;
    private RecyclerView mRecyclerView;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private List<FriendsProfile> mFreeData;
    private FreeAdapter mFreeAdapter;



    public FreeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFreeData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_free, container, false);
        rootView.setTag(TAG);

        mContext = rootView.getContext();
        mRecyclerView = rootView.findViewById(R.id.recyclerview_free);

        int scrollPosition = 0;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        mFreeAdapter = new FreeAdapter(getContext(),mFreeData);
        mRecyclerView.setAdapter(mFreeAdapter);

//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(mRecyclerView);

        mFreeData = FriendsUtils.loadProfiles(mContext);
        for(int i = 0; i <mFreeData.size();i++){
            mFreeAdapter.addView(mFreeData.get(i),i);

        }

        showSwitch(rootView);

        return rootView;
    }

    private void showSwitch(View rootView){
        de.hdodenhof.circleimageview.CircleImageView mProfileView = (de.hdodenhof.circleimageview.CircleImageView) rootView.findViewById(R.id.free_selfImageView);
        int id = getResources().getIdentifier("rushabh_sheth", "drawable", getContext().getPackageName());
        String path = "android.resource://" + getContext().getPackageName() + "/" + id;

        if(path!=null){
            Uri uri = Uri.parse(path);
            Glide.with(mContext)
                    .load(uri)
                    .into(mProfileView);
        }

        TextView free_selfTextView = (TextView) rootView.findViewById(R.id.free_selfTextView);
        free_selfTextView.setText(mContext.getResources().getString(R.string.stringUserName));
    }
}
