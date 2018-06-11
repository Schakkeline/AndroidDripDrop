package com.buchmaier.jacqueline.mamiwata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jacqueline on 07.06.18.
 */


public class ResetWaterService extends BroadcastReceiver {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("settings");

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("ResetWaterService", "Broadcasr onReceive: fired ");

    }

}
