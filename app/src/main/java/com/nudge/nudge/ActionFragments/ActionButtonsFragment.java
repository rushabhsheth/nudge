package com.nudge.nudge.ActionFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.nudge.nudge.FriendsTab.FriendsFragment;
import com.nudge.nudge.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by rushabh on 11/10/17.
 */

public class ActionButtonsFragment extends Fragment{

    private static final String TAG = "ActionButtons";

//    @BindView(R.id.rejectBtn)
//    ImageButton reject_btn;
//
//    @BindView(R.id.nudgeBtn)
//    ImageButton nudge_btn;
//
//    @BindView(R.id.messageBtn)
//    ImageButton message_btn;

    private ImageButton reject_btn;
    private ImageButton nudge_btn;
    private ImageButton message_btn;

    private SwipePlaceHolderView mSwipeView;

    private Unbinder unbinder;

    private onClickListener mOnClickListener;

    public ActionButtonsFragment() {
       //Empty Contructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actionbuttons, container, false);

//        ButterKnife.bind(this, rootView);
        nudge_btn = rootView.findViewById(R.id.nudgeBtn);
        reject_btn = rootView.findViewById(R.id.rejectBtn);
        message_btn = rootView.findViewById(R.id.messageBtn);



        reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSwipeView!=null) {
                    mSwipeView.doSwipe(false);
                }
            }
        });

        nudge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSwipeView!=null) {
                    mSwipeView.doSwipe(true);
                }
            }
        });

        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onMessageBtnClick();
            }
        });

        return rootView;
    }


//    @Click(R.id.rejectBtn)
//    private void rejectButtonClicked(View view){
//        if(mSwipeView!=null) {
//            mSwipeView.doSwipe(false);
//        }
//    }
//
//    @Click(R.id.nudgeBtn)
//    private void nudgeButtonClicked(){
//        if(mSwipeView!=null) {
//                    mSwipeView.doSwipe(true);
//        }
//    }
//
//    @Click(R.id.messageBtn)
//    private void messageButtonClicked(){
//        mOnClickListener.onMessageBtnClick();
//    }

    public void setButtonClickListener(onClickListener listener){
        this.mOnClickListener = listener;
    }

    public void setSwipePlaceHolderView(SwipePlaceHolderView swipePlaceHolderView){
        this.mSwipeView = swipePlaceHolderView;
    }

    public interface onClickListener{
        void onMessageBtnClick();
    }

}
