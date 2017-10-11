package com.nudge.nudge.Calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nudge.nudge.R;

/**
 * Created by rushabh on 05/10/17.
 */

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_star);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.activity_calendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
