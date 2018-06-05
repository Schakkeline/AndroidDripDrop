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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonationsFragment extends Fragment {

    // Donation
    private TextView textViewDonations;
    Float valueMyDonations;

    // Read from the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("settings");
    DatabaseReference dbMyDonation = database.getReference("settings").child("myDonation");

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
                valueMyDonations = dataSnapshot.child("myDonation").getValue(Float.class);
                textViewDonations.setText(String.valueOf(valueMyDonations + "$  today"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });

        // Donate Button - reset Donations to zero
        // TODO: Nice to have -> real donations
        Button donate = v.findViewById(R.id.donate);
        donate.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: This is not working - because i always calculate onDataChange
                dbMyDonation.setValue(0);
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
