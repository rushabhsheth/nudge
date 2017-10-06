package com.nudge.nudge.StarContacts;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nudge.nudge.R;

import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class StarContactsAdapter extends RecyclerView.Adapter<StarContactsAdapter.ViewHolder> {

    private static final String TAG = "StarContactsAdapter";

    Context mContext;
    private List<StarContactsClass> mDataSet;

    //Viewholder to hold item_main
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final de.hdodenhof.circleimageview.CircleImageView contactImageView;
        private final TextView contactNameTextView;
        private final ImageButton mStarButton;


        public ViewHolder(View v) {
            super(v);


            contactImageView = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.contactImageView);
            contactNameTextView = (TextView) v.findViewById(R.id.contactTextView);
            mStarButton = (ImageButton) v.findViewById(R.id.StarContactButton);

        }

        public de.hdodenhof.circleimageview.CircleImageView getContactImageView()

        {
            return contactImageView;
        }

        public TextView getContactNameTextView()

        {
            return contactNameTextView;
        }

        public ImageButton getStarButton(){
            return mStarButton;
        }

    }

    //Constructor
    public StarContactsAdapter(Context context,List<StarContactsClass> dataSet) {
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_starcontacts, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.d(TAG, "Element " + position + " set.");
        final ViewHolder finalHolder = viewHolder;
        Context context = finalHolder.itemView.getContext();
        Resources res = finalHolder.itemView.getContext().getResources();

        final StarContactsClass starContact = mDataSet.get(position);

        String userName = starContact.getContact_name();
        if(userName != null){
            finalHolder.getContactNameTextView().setText(userName);
        }

        String userProfileImage = starContact.getProfile_image_uri();
        if(userProfileImage!=null){
            Uri uri = Uri.parse(userProfileImage);
            Glide.with(context)
                    .load(uri)
                    .into(finalHolder.getContactImageView());
        }



        finalHolder.getStarButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean starPressed = starContact.getStarPressed();
                        if (!starPressed) {
                            finalHolder.getStarButton().setImageResource(R.mipmap.star_blue_filled);
                            starContact.setStarPressed(true);
                        }
                        else {
                            finalHolder.getStarButton().setImageResource(R.mipmap.star_blue_empty);
                            starContact.setStarPressed(false);
                        }
                    }
                }
        );

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addItem(StarContactsClass dataObj, int index) {
        mDataSet.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }


}
