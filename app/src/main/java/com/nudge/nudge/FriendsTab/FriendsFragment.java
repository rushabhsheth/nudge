package com.nudge.nudge.FriendsTab;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.SwipeViewBinder;
import com.nudge.nudge.ActionFragments.ActionButtonsFragment;
import com.nudge.nudge.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private ActionButtonsFragment mActionButtons;


    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mSwipeView = (SwipePlaceHolderView) rootView.findViewById(R.id.swipeView);
        mContext = rootView.getContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(2)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setViewGravity(Gravity.TOP)
                        .setViewGravity(Gravity.CENTER_HORIZONTAL)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.nudge_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.nudge_swipe_out_msg_view));


        for(FriendsProfileClass profile : FriendsUtils.loadProfiles(this.getContext())){
            mSwipeView.addView(new FriendsCard(mContext, profile, mSwipeView));
        }

        String tag_actionbuttons_friendtab = "tag_actionbuttons_friendtab";
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        mActionButtons = new ActionButtonsFragment();
        childFragTrans.add(R.id.fragment_friendstab_actionbuttons, mActionButtons,tag_actionbuttons_friendtab);
        childFragTrans.addToBackStack(null);
        childFragTrans.commit();

//        FragmentManager f = getActivity().getSupportFragmentManager();
//        mActionButtons = (ActionButtonsFragment) f.findFragmentByTag(getResources().getString(R.string.fragment_actionbuttons));

        Log.d(TAG, "Action buttons fragment id: "+String.valueOf(mActionButtons));

        if (mActionButtons != null) {

            mActionButtons.setSwipePlaceHolderView(mSwipeView);
        }


        return rootView;
    }





}
