package com.nudge.nudge.ActionFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;

import com.nudge.nudge.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rushabh on 11/10/17.
 */

public class MessageDialogFragment extends DialogFragment {

    public static final String TAG = "MessageDialogFragment";

    public MessageDialogFragment(){
        //Empty Constructor
    }

    private View mRootView;

    private SendListener mSendListener;

    @BindView(R.id.message_form_text)
    EditText mMessageText;

    String messageText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_message, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SendListener) {
            mSendListener = (SendListener) context;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mMessageText.setText(messageText);       // getText(R.string.hint_send_message));

    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.button_send)
    public void onSendClicked() {
        if (mSendListener != null) {
            String message = mMessageText.getText().toString();
            mSendListener.onSendClickedMessageDialog(message);
        }

        dismiss();
    }

    public interface SendListener {

        void onSendClickedMessageDialog(String message);

    }

    public void setSendListener(SendListener listener){
        this.mSendListener = listener;
    }

    public void setMessageDialogText(String text){
        messageText = text;
    }

}
