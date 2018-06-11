package com.buchmaier.jacqueline.mamiwata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jacqueline on 11.06.18.
 */

public class ReminderAlarmManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Show Push Reminder to drink water
        Intent i = new Intent(context, ShowNotifications.class);
        context.startService(i);
        context.stopService(i);

    }

}
