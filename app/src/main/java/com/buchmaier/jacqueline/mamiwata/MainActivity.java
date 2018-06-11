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
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

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


        /* ****************************** USER TEST ****************************** */
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Creating new user node, which returns the unique key value
        // new user node would be /users/$userid/
        String userId = mDatabase.push().getKey();

        // creating user object
        User user = new User("Jacqueline Buchmaier", "foo@bar.info");

        // pushing user to 'users' node using the userId
        mDatabase.child(userId).setValue(user);

        /* ****************************** USER TEST ****************************** */

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

        // setRepeating() lets you specify a precise custom interval--in this case, 1 minute.
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, alarmIntent);

        // setRepeating() lets you specify a precise custom interval--in this case, 1 minute.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();

    }

}