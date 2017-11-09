package com.nudge.nudge.FreeTab;

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
import com.nudge.nudge.Data.Models.ContactsClass;
import com.nudge.nudge.R;

import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class FreeAdapter extends RecyclerView.Adapter<FreeAdapter.ViewHolder> {

    private static final String TAG = "FreeAdapter";

    Context mContext;
    private List<ContactsClass> mDataSet;

    //Viewholder to hold item_main
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final de.hdodenhof.circleimageview.CircleImageView free_contactsImageView;
        private final TextView free_nameView;
        private final ImageButton free_nudgeBtn;
//        private final ImageButton free_callBtn;
//        private final ImageButton free_messageBtn;


        public ViewHolder(View v) {
            super(v);

            free_contactsImageView = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.free_contactsImageView);
            free_nameView = (TextView) v.findViewById(R.id.free_nameTextView);
            free_nudgeBtn = (ImageButton) v.findViewById(R.id.free_nudgeBtn);
//            free_callBtn = (ImageButton) v.findViewById(R.id.free_callBtn);
//            free_messageBtn = (ImageButton) v.findViewById(R.id.free_messageBtn);
        }

        public de.hdodenhof.circleimageview.CircleImageView getFree_contactImageView(){
            return free_contactsImageView;
        }

        public TextView getFree_nameView()

        {
            return free_nameView;
        }
        public ImageButton getFree_nudgeBtn(){
            return free_nudgeBtn;
        }

//        public ImageButton getFree_callBtn(){
//            return free_callBtn;
//        }
//        public ImageButton getFree_messageBtn(){
//            return free_messageBtn;
//        }

    }

    //Constructor
    public FreeAdapter(Context context, List<ContactsClass> dataSet) {
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_free, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.d(TAG, "Element " + position + " set.");
        final ViewHolder finalHolder = viewHolder;
        Context context = finalHolder.itemView.getContext();
        Resources res = finalHolder.itemView.getContext().getResources();

        final ContactsClass friendsProfileClass = mDataSet.get(position);

        String userName = friendsProfileClass.getContactName();
        if(userName != null){
            finalHolder.getFree_nameView().setText(userName);
        }


        String userProfileImage = friendsProfileClass.getProfileImageUri();
        if(userProfileImage!=null){
            Uri uri = Uri.parse(userProfileImage);
            Glide.with(context)
                    .load(uri)
                    .into(finalHolder.getFree_contactImageView());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addView(ContactsClass dataObj, int index) {
        mDataSet.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteView(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }


}
