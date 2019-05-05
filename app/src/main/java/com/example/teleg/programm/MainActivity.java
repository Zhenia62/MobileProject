package com.example.teleg.programm;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import fragments.CalenderFragment;
import fragments.NewsFragment;
import fragments.ScheduleFragmentMain;
import fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_schedule:
                        mTextMessage.setText(R.string.schedule);
                        loadFragment(ScheduleFragmentMain.newInstance());
                        return true;
                    case R.id.action_news:
                            mTextMessage.setText(R.string.news);
                            loadFragment(NewsFragment.newInstance());
                            return true;
                        //mTextMessage.setText(R.string.news);
                        //loadFragment(NewsFragment.newInstance());
                        //return true;
                    case R.id.action_calender:
                        mTextMessage.setText(R.string.calender);
                        loadFragment(CalenderFragment.newInstance());
                        return true;
                    case R.id.action_tools:
                        mTextMessage.setText(R.string.tools);
                        loadFragment(SettingsFragment.newInstance());
                        return true;
                }
                return false;
            }
        };



    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content,  fragment);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(ScheduleFragmentMain.newInstance());

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i(TAG,"onConfigurationChanged: orientation = portrait");
        }else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.i(TAG,"onConfigurationChanged: orientation = landscape");
        }
    }
}
