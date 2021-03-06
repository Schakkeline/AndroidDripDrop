package com.buchmaier.jacqueline.mamiwata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jacqueline on 07.06.18.
 */


public class ResetWaterService extends BroadcastReceiver {

    // Read from the database
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = firebaseUser.getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("ResetWaterService", "Broadcasr onReceive: fired ");
        db.child("currentWater").setValue(0);
        db.child("DonatedToday").setValue(false);

    }

}
