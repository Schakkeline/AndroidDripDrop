package com.buchmaier.jacqueline.mamiwata;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonationsFragment extends Fragment {

    // Donation
    private TextView textViewDonations;

    Float valueDailyDonation;
    Boolean valueDonatedToday;

    // Donate button
    Button donate;

    // Read from the database
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = firebaseUser.getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
    DatabaseReference dbDonatedToday = db.child("DonatedToday");

    DatabaseReference dbDailyDonation = db.child("DailyDonation");
    Integer valueUserLiterGoal;
    Integer valueUserLiterCurrent;

    public DonationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_donations, container, false);


        // Get data from database
        textViewDonations = v.findViewById(R.id.donations);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Donations from Database
                valueDailyDonation = dataSnapshot.child("DailyDonation").getValue(Float.class);
                valueDonatedToday = dataSnapshot.child("DonatedToday").getValue(Boolean.class);

                valueUserLiterCurrent = dataSnapshot.child("currentWater").getValue(Integer.class);
                valueUserLiterGoal = dataSnapshot.child("myWater").getValue(Integer.class);

                // reachGoalOrDonate TextView
                if (valueUserLiterCurrent < valueUserLiterGoal){
                    textViewDonations.setText(String.valueOf(valueDailyDonation + "$  today"));
                } else {
                    textViewDonations.setText(String.valueOf("Great! You reached your goal today!"));
                    donate.setEnabled(false);                }
                if (valueDonatedToday && valueUserLiterCurrent < valueUserLiterGoal){
                    textViewDonations.setText(String.valueOf("Thanks, but don't forget to drink!"));
                    donate.setEnabled(false);                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });

        // Donate Button - reset Donations to zero
        // TODO: Nice to have -> real donations
        donate = v.findViewById(R.id.donate);
        donate.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Donated today = true, daily donation to zero and fullDonation to zero
                dbDonatedToday.setValue(true);
                dbDailyDonation.setValue(0);
            }
        });

        // Read more link to unicef
        Button readMore = v.findViewById(R.id.readMore);
        readMore.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unicef.org/"));
                startActivity(browserIntent);
            }
        });

        return v;
    }

}
