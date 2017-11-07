package com.nudge.nudge.NudgesTab;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nudge.nudge.Data.Database.NudgeClass;
import com.nudge.nudge.R;

import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class NudgesAdapter extends RecyclerView.Adapter<NudgesAdapter.ViewHolder> {

    private static final String TAG = "StarContactsAdapter";

    Context mContext;
    private List<NudgeClass> mDataSet;

    //Viewholder to hold item_main
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final de.hdodenhof.circleimageview.CircleImageView nudges_contactImageView;
        private final TextView nudges_nameView;
        private final TextView nudges_timeView;
        private final ImageButton nudges_nudgeBtn;
        private final ImageButton nudges_callBtn;
        private final ImageButton nudges_rejectBtn;
        private final ImageButton nudges_scheduleBtn;


        public ViewHolder(View v) {
            super(v);

            nudges_contactImageView = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.nudges_contactImageView);
            nudges_nameView = (TextView) v.findViewById(R.id.nudges_nameTextView);
            nudges_timeView = (TextView) v.findViewById(R.id.nudges_timeTextView);
            nudges_nudgeBtn = (ImageButton) v.findViewById(R.id.nudges_nudgeBtn);
            nudges_callBtn = (ImageButton) v.findViewById(R.id.nudges_callBtn);
            nudges_scheduleBtn = (ImageButton) v.findViewById(R.id.nudges_scheduleBtn);
            nudges_rejectBtn = (ImageButton) v.findViewById(R.id.nudges_rejectBtn);
        }

        public de.hdodenhof.circleimageview.CircleImageView getNudges_contactImageView(){
            return nudges_contactImageView;
        }

        public TextView getNudges_nameView()

        {
            return nudges_nameView;
        }
        public TextView getNudges_timeView()

        {
            return nudges_timeView;
        }

        public ImageButton getNudges_nudgeBtn(){
            return nudges_nudgeBtn;
        }
        public ImageButton getNudges_callBtn(){
            return nudges_callBtn;
        }
        public ImageButton getNudges_rejectBtn(){
            return nudges_rejectBtn;
        }
        public ImageButton getNudges_scheduleBtn(){
            return nudges_scheduleBtn;
        }

    }

    //Constructor
    public NudgesAdapter(Context context, List<NudgeClass> dataSet) {
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_nudges, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.d(TAG, "Element " + position + " set.");
        final ViewHolder finalHolder = viewHolder;
        Context context = finalHolder.itemView.getContext();
        Resources res = finalHolder.itemView.getContext().getResources();

        final NudgeClass nudgeClass = mDataSet.get(position);

        String userName = nudgeClass.getReceiverName();
        if(userName != null){
            finalHolder.getNudges_nameView().setText(userName);
        }

        String timestamp = nudgeClass.getTimestamp().toString();
        if(timestamp != null){
            finalHolder.getNudges_timeView().setText(timestamp);
        }

        String userProfileImage = nudgeClass.getReceiverImageUrl();
        if(userProfileImage!=null){
            Uri uri = Uri.parse(userProfileImage);
            Glide.with(context)
                    .load(uri)
                    .into(finalHolder.getNudges_contactImageView());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addView(NudgeClass dataObj, int index) {
        mDataSet.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteView(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }


}
