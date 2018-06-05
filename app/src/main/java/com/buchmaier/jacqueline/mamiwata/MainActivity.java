package com.buchmaier.jacqueline.mamiwata;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;

public class MainActivity extends AppCompatActivity {

    Intent context;

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

        // Send drink Water alarm as push notification
        // TODO: send push only in time frame - user dont want to drink at night ;)
        Intent notificationIntent = new Intent(this, ShowNotifications.class);
        PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(contentIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                 + AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, contentIntent);

    }

}