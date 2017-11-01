package com.nudge.nudge.FriendProfile;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nudge.nudge.Data.Database.ContactsClass;
import com.nudge.nudge.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class FriendProfileAdapter extends RecyclerView.Adapter<FriendProfileAdapter.ViewHolder> {

    private static final String TAG = "FriendProfileAdapter";

    Context mContext;
    private ContactsClass mFriendData;
    private List<String> mFriendImageList;

    //Viewholder to hold item_main
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView friendprofile_ImageView;


        public ViewHolder(View v) {
            super(v);

            friendprofile_ImageView = (ImageView) v.findViewById(R.id.image_friendprofile);
        }

        public ImageView getFriendprofile_ImageView(){
            return friendprofile_ImageView;
        }

    }

    //Constructor
    public FriendProfileAdapter(Context context, ContactsClass friendData) {
        this.mFriendData = friendData;
        this.mContext = context;
        mFriendImageList = new ArrayList<>();
        mFriendImageList = mFriendData.getProfileImageList();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_friendprofileimage, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

//        Log.d(TAG, "Element " + position + " set.");
        final ViewHolder finalHolder = viewHolder;
        Context context = viewHolder.itemView.getContext();
        Resources res = finalHolder.itemView.getContext().getResources();


        finalHolder.getFriendprofile_ImageView().setImageDrawable(null);

        String userProfileImage = mFriendImageList.get(position);
        Log.d(TAG,String.valueOf(position)+" " + userProfileImage);
        if(userProfileImage!=null){
            Uri uri = Uri.parse(userProfileImage);
            Glide.with(context)
                    .load(uri)
                    .into(finalHolder.getFriendprofile_ImageView());
        }
        else {
            Glide.with(context).clear(finalHolder.getFriendprofile_ImageView());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFriendImageList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addImage(String imageUri, int index) {
        mFriendImageList.add(imageUri);
        notifyItemInserted(index);
    }

    public void deleteView(int index) {
        mFriendImageList.remove(index);
        notifyItemRemoved(index);
    }


}
