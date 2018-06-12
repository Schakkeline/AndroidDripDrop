package com.buchmaier.jacqueline.mamiwata;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener authListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment selectedFragment1 = new HomeFragment();
                    transaction.replace(R.id.content, selectedFragment1);
                    break;
                case R.id.navigation_donations:
                    DonationsFragment selectedFragment2 = new DonationsFragment();
                    transaction.replace(R.id.content, selectedFragment2);
                    break;
                case R.id.navigation_settings:
                    SettingsFragment selectedFragment3 = new SettingsFragment();
                    transaction.replace(R.id.content, selectedFragment3);
                    break;
            }

            transaction.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bottom Navigation
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Display fragment on start - if you dont do this, you need to tap on the navigation
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        HomeFragment selectedFragment1 = new HomeFragment();
        transaction.replace(R.id.content, selectedFragment1);
        transaction.commit();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("FirebaseUser", "User: " + user);

        if (user == null) {
            // user auth state is changed - user is null
            // launch login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

            }
        };

        // Send drink Water alarm as push notification
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderAlarmManager.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm to start at 2:55 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // TODO: Set custom start time
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 55);

        // setRepeating() lets you specify a precise custom interval--in this case, 1 hour.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, alarmIntent);

        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();

    }

}