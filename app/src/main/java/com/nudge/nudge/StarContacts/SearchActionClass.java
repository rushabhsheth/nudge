package com.nudge.nudge.StarContacts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nudge.nudge.R;

import java.lang.reflect.Field;

/**
 * Created by rushabh on 16/10/17.
 */

public class SearchActionClass{

    private static final String TAG = "SearchActionClass";

    private Context mContext;
    private Menu search_menu;
    private MenuItem item_search;
    private Toolbar mToolbar;

    private SearchView searchView;

    SearchActionClass(Context context, Toolbar toolbar){

        this.mContext = context;
        this.mToolbar = toolbar;
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        searchMenuItem = menu.findItem(R.id.action_search);
//
//        searchView = (SearchView) searchMenuItem.getActionView();
//        searchView.setSearchableInfo(searchManager.
//                getSearchableInfo(getActivity().getComponentName()));
//        searchView.setSubmitButtonEnabled(false);
//        searchView.setOnQueryTextListener(this);
    }

    public void initSearchView()
    {
         searchView = (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close_grey);


        // set hint and the text colors

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search Friends");
        txtSearch.setHintTextColor(ContextCompat.getColor(mContext,R.color.colorTextLight));
        txtSearch.setTextColor(ContextCompat.getColor(mContext,R.color.colorText));


        // set the cursor
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                Log.i(TAG,"query " + query);

            }

        });


    }

//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String query) {
////        final List<ContactsClass> filteredModelList = filter(mStarContactsData_allcontacts, query);
////        mStarContactsAdapter.replaceAll(filteredModelList);
////        mRVstarcontacts.scrollToPosition(0);
//        return true;
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(Toolbar toolbar, int posFromRight, boolean containsOverflow, final boolean isShow)
    {
        final View myView =  toolbar;

        int width=myView.getWidth();

        if(posFromRight>0)
            width-=(posFromRight* mContext.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))-(mContext.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)/ 2);
        if(containsOverflow)
            width-= myView.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx=width;
        int cy=myView.getHeight()/2;

        Animator anim;
        if(isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0,(float)width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float)width, 0);

        anim.setDuration((long)220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow)
                {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if(isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();

    }

    public void setSearchToolbar(){


        if (mToolbar != null) {
            mToolbar.inflateMenu(R.menu.menu_search);
            search_menu = mToolbar.getMenu();

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(mToolbar,1,true,false);
                    else
                        mToolbar.setVisibility(View.GONE);
                }
            });


            item_search = search_menu.findItem(R.id.action_filter_search);

            item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(mToolbar,1,true,false);
                    }
                    else
                        mToolbar.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });

            initSearchView();


        } else
            Log.d(TAG, "setSearchToolbar: NULL");

    }

    public void actionSearch (){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            circleReveal(mToolbar,1,true,true);
        else
            mToolbar.setVisibility(View.VISIBLE);

        searchView.setIconified(false);
        item_search.expandActionView();
    }

    public void actionClose(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleReveal(mToolbar,1,true,false);
        }
        else
            mToolbar.setVisibility(View.GONE);

        item_search.collapseActionView();
        searchView.setIconified(true);
    }

    public SearchView getSearchView(){
        return searchView;
    }

}
