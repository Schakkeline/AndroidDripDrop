package com.buchmaier.jacqueline.mamiwata;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment implements OnClickListener{

    TextView yourWater;
    TextView userLiterCurrent;
    TextView userLiterGoal;
    TextView reachGoalOrDonate;

    Integer dbUserLiterGoal;
    Float userLiterGoalLiter;

    Integer dbUserLiterCurrent;

    Float yourWaterPercentFloat;
    Integer yourWaterPercent;

    Integer missingWater;
    Float valueMyDonations;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("settings");

    private static final String TAG = "Service";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Percent of water the user drank today
        yourWater = v.findViewById(R.id.text_home_percent);

        // How much did the user drink
        userLiterCurrent = v.findViewById(R.id.userLiterCurrent);

        // Drink Goal from user
        userLiterGoal = v.findViewById(R.id.userLiterGoal);

        // reachGoalOrDonate
        reachGoalOrDonate = v.findViewById(R.id.reachGoalOrDonate);

        // open BottomSheet onClick
        Button b = v.findViewById(R.id.button_addDrink);
        b.setOnClickListener(this);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Use only two digits after point for better reading
                DecimalFormat twoDForm = new DecimalFormat("#.##");

                // User should drink this amount in liter
                dbUserLiterGoal = dataSnapshot.child("myWater").getValue(Integer.class);
                userLiterGoalLiter = (float) dbUserLiterGoal / 1000;
                Double userLiterGoalLiterRound = Double.valueOf(twoDForm.format(userLiterGoalLiter));
                userLiterGoal.setText(String.valueOf(" from " + userLiterGoalLiterRound + " liter"));

                // User drank this amount so far in ml
                dbUserLiterCurrent = dataSnapshot.child("currentWater").getValue(Integer.class);
                userLiterCurrent.setText(String.valueOf(dbUserLiterCurrent + "ml"));

                // Percent
                yourWaterPercentFloat = (float) dbUserLiterCurrent / dbUserLiterGoal *100;
                yourWaterPercent = Math.round(yourWaterPercentFloat);
                yourWater.setText(String.valueOf(yourWaterPercent + " %"));

                // Formula for Donation
                // TODO: Only calculate if not reset to zero from user
                missingWater = dbUserLiterGoal - dbUserLiterCurrent;
                Float x = (float)Math.round(missingWater ) / 10000;
                Double roundetMissingWater = Double.valueOf(twoDForm.format(x));
                DatabaseReference dbMyDonation = database.getReference("settings").child("myDonation");
                dbMyDonation.setValue(roundetMissingWater);

                // reachGoalOrDonate TextView
                valueMyDonations = dataSnapshot.child("myDonation").getValue(Float.class);
                if (valueMyDonations > 0){
                    reachGoalOrDonate.setText(String.valueOf("Reach your goal or donate " + valueMyDonations +"$"));
                } else {
                    reachGoalOrDonate.setText(String.valueOf("Great! You reached your goal today!"));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_addDrink:
                Log.d(TAG, "Button clicked");
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
                break;
        }
    }

}
