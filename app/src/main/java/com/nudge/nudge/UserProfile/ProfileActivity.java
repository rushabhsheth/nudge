package com.nudge.nudge.UserProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.nudge.nudge.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rushabh on 05/10/17.
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "User Profile Activity";

    @BindView(R.id.toolbar_userprofile)
    Toolbar mToolbar;

    @BindView(R.id.profile_signoutButton)
    Button mSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(R.string.activity_userprofile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        initSignOutListener();
    }

    private void initSignOutListener(){
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("SignOut","Yes");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

}
