package com.nudge.nudge.ActionFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.nudge.nudge.FriendsTab.FriendsFragment;
import com.nudge.nudge.R;

/**
 * Created by rushabh on 11/10/17.
 */

public class ActionButtonsFragment extends Fragment{

    private static final String TAG = "ActionButtons";
    private ImageButton reject_btn;
    private ImageButton nudge_btn;
    private SwipePlaceHolderView mSwipeView;

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

        nudge_btn = rootView.findViewById(R.id.nudges_nudgeBtn);
        reject_btn = rootView.findViewById(R.id.rejectBtn);

        rootView.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSwipeView!=null) {
                    mSwipeView.doSwipe(false);
                }
            }
        });

        rootView.findViewById(R.id.nudgeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSwipeView!=null) {
                    mSwipeView.doSwipe(true);
                }
            }
        });

        return rootView;
    }

    public void setSwipePlaceHolderView(SwipePlaceHolderView swipePlaceHolderView){
        this.mSwipeView = swipePlaceHolderView;
    }

}
