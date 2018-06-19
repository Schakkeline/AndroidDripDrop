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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener authListener;
    // Read from the database
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    // Push Notifications
    Boolean sendNotis;
    AlarmManager alarmMgr;
    Intent intent;
    PendingIntent alarmIntent;
    Calendar calendar;

    // Reset at midnight
    AlarmManager alarmMgrReset;
    Intent intentReset;
    PendingIntent alarmIntentReset;
    Calendar calendarReset;

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

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("FirebaseUser", "User: " + user);

        if (user != null) {
            String email = user.getEmail();
            String uid = user.getUid();
            Log.d("FirebaseUser", "User Mail: " + email);
            Log.d("FirebaseUser", "User UID: " + uid);
        }


        if (user == null) {
            // user auth state is changed - user is null
            // launch login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            String uid = firebaseUser.getUid();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            // Display fragment on start - if you dont do this, you need to tap on the navigation
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            HomeFragment selectedFragment1 = new HomeFragment();
            transaction.replace(R.id.content, selectedFragment1);
            transaction.commit();

            // Reset water at midnight
            alarmMgrReset = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            intentReset = new Intent(this, ResetWaterService.class);
            alarmIntentReset = PendingIntent.getBroadcast(this, 0, intentReset, 0);
            // Set the alarm to start at midnight
            calendarReset = Calendar.getInstance();
            calendarReset.setTimeInMillis(System.currentTimeMillis());
            int curHrReset = calendarReset.get(Calendar.HOUR_OF_DAY);

            // Checking whether current hour is over 24
            if (curHrReset < 24) {
                //setting the date to the next day
                calendarReset.add(Calendar.DATE, 1);
            }

            calendarReset.set(Calendar.HOUR_OF_DAY, 23);
            calendarReset.set(Calendar.MINUTE, 59);
            alarmMgrReset.setRepeating(AlarmManager.RTC_WAKEUP, calendarReset.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentReset);

            // Send drink Water alarm as push notification
            alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            intent = new Intent(this, ReminderAlarmManager.class);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            // Set the alarm to start at 2:55 a.m.
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // TODO: Set custom end time
            int curHr = calendar.get(Calendar.HOUR_OF_DAY);

            // Checking whether current hour is over 20
            if (curHr >= 20) {
                //setting the date to the next day
                calendar.add(Calendar.DATE, 1);
            }

            // TODO: Set custom start time
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 59);

            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    sendNotis = dataSnapshot.child("notifications").getValue(Boolean.class);
                    if (sendNotis){
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
                    } else {
                        alarmMgr.cancel(alarmIntent);
                    }

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });

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

    }

}