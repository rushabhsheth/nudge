package com.nudge.nudge.StarContacts;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nudge.nudge.ContactsData.ContactsClass;
import com.nudge.nudge.R;

import java.util.Comparator;
import java.util.List;

/**
 * Created by rushabh on 06/10/17.
 */

public class StarContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "StarContactsAdapter";

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 0;

    Context mContext;
    private List<ContactsClass> mDataSet_allcontacts;
    private List<ContactsClass> mDataSet_favourites;
    int totalItems;
    private final Comparator<ContactsClass> mComparator;

    //Constructor
    public StarContactsAdapter(Context context,List<ContactsClass> dataSet_allcontacts,List<ContactsClass> dataSet_favourites, Comparator<ContactsClass> comparator) {
        this.mDataSet_allcontacts = dataSet_allcontacts;
        this.mDataSet_favourites = dataSet_favourites;
        this.mContext = context;
        this.mComparator = comparator;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_HEADER){
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_starcontacts_header, viewGroup, false);
            return new VHHeader(v);
        }

        else if (viewType == TYPE_ITEM) {
            // Create a new view.
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_starcontacts_contact, viewGroup, false);
            return new VHItem(v);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof VHItem) {
            loadVHItem(viewHolder,position);

        } else if (viewHolder instanceof VHHeader) {
            loadVHHeader(viewHolder,position);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        totalItems = mDataSet_allcontacts.size() + mDataSet_favourites.size() + 2; //2 headers - favourites & all contacts
//        totalItems = mSortedList.size() + mDataSet_favourites.size() + 2; //2 headers - favourites & all contacts
//        totalItems = mDataSet_favourites.size();
        return totalItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        if (position==0 || position == (mDataSet_favourites.size()+1)){
            return true;
        }
        else return false;
    }

    private ContactsClass getItem(int position){
        if (position< mDataSet_favourites.size()+1){
            return mDataSet_favourites.get(position);
        }
        else {
            return mDataSet_allcontacts.get(position - mDataSet_favourites.size()-2); //-2 is for headers
//              return mSortedList.get(position - mDataSet_favourites.size()-2); //-2 is for
        }
    }

    private String getHeader(int position, Resources res){
        if (position==0){
            return res.getString(R.string.string_favourites);
        }
        else {
            return res.getString(R.string.string_allcontacts);
        }
    }


    public void addItem(ContactsClass dataObj, int index) {
        mDataSet_favourites.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataSet_favourites.remove(index);
        notifyItemRemoved(index);
    }

    private void loadVHItem(RecyclerView.ViewHolder viewHolder, int position){

        final VHItem finalholder = (VHItem) viewHolder;
        //Log.d(TAG, "Element " + position + " set.");
        Context context = finalholder.itemView.getContext();
        Resources res = finalholder.itemView.getContext().getResources();

        final ContactsClass starContact = getItem(position);

        String userName = starContact.getContact_name();
        if(userName != null){
            finalholder.getContactNameTextView().setText(userName);
        }

        Glide.with(context).clear(finalholder.getContactImageView());
        String userProfileImage = starContact.getProfile_image_uri();
        if(userProfileImage!=null){
            Uri uri = Uri.parse(userProfileImage);
            Glide.with(context)
                    .load(uri)
                    .into(finalholder.getContactImageView());
        }



        finalholder.getStarButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean starPressed = starContact.getStarPressed();
                        if (!starPressed) {
                            finalholder.getStarButton().setImageResource(R.drawable.ic_star_blue);
                            starContact.setStarPressed(true);
                        }
                        else {
                            finalholder.getStarButton().setImageResource(R.drawable.ic_star_hollow);
                            starContact.setStarPressed(false);
                        }
                    }
                }
        );

    }

    private void loadVHHeader(RecyclerView.ViewHolder viewHolder, int position){

        final VHHeader finalholder = (VHHeader) viewHolder;
        Resources res = finalholder.itemView.getContext().getResources();

        String header = getHeader(position,res);
        if(header != null){
            finalholder.getHeaderTextView().setText(header);
        }

    }

    class VHItem extends RecyclerView.ViewHolder {

        private final de.hdodenhof.circleimageview.CircleImageView contactImageView;
        private final TextView contactNameTextView;
        private final ImageButton mStarButton;


        public VHItem(View v) {
            super(v);

            contactImageView = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.contactImageView);
            contactNameTextView = (TextView) v.findViewById(R.id.contactTextView);
            mStarButton = (ImageButton) v.findViewById(R.id.StarContactButton);
        }

        public de.hdodenhof.circleimageview.CircleImageView getContactImageView() {
            return contactImageView;
        }

        public TextView getContactNameTextView(){
            return contactNameTextView;
        }

        public ImageButton getStarButton(){
            return mStarButton;
        }

    }

    class VHHeader extends RecyclerView.ViewHolder {

        private final TextView headerTextView;

        public VHHeader(View v) {
            super(v);
            headerTextView = (TextView) v.findViewById(R.id.starcontacts_header);

        }

        public TextView getHeaderTextView(){
            return headerTextView;
        }

    }



    private final SortedList<ContactsClass> mSortedList = new SortedList<>(ContactsClass.class, new SortedList.Callback<ContactsClass>() {
        @Override
        public int compare(ContactsClass a, ContactsClass b) {
            return mComparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(ContactsClass oldItem, ContactsClass newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(ContactsClass item1, ContactsClass item2) {
            return item1.getId() == item2.getId();
        }
    });

    public void add(ContactsClass model) {
        mSortedList.add(model);
    }

    public void remove(ContactsClass model) {
        mSortedList.remove(model);
    }

    public void add(List<ContactsClass> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<ContactsClass> models) {
        mSortedList.beginBatchedUpdates();
        for (ContactsClass model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<ContactsClass> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final ContactsClass model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

}
