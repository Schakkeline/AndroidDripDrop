package com.buchmaier.jacqueline.mamiwata;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment implements OnClickListener{

    TextView yourWater;
    TextView userLiterGoal;
    TextView reachGoalOrDonate;

    Integer dbUserLiterGoal;
    Float userLiterGoalLiter;

    Integer dbUserLiterCurrent;

    Float yourWaterPercentFloat;
    Integer yourWaterPercent;

    Integer missingWater;

    Float valueDailyDonation;

    Boolean valueDonatedToday;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = firebaseUser.getUid();
    DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    DatabaseReference dbDailyDonation = DataRef.child("DailyDonation");

    View v;


    private static final String TAG = "Service";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);



        // Percent of water the user drank today
        yourWater = v.findViewById(R.id.text_home_percent);

        // Drink Goal from user
        userLiterGoal = v.findViewById(R.id.userLiterGoal);

        // reachGoalOrDonate
        reachGoalOrDonate = v.findViewById(R.id.reachGoalOrDonate);

        // open BottomSheet onClick
        Button b = v.findViewById(R.id.button_addDrink);
        b.setOnClickListener(this);

        DataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Use only two digits after point for better reading
                DecimalFormat twoDForm = new DecimalFormat("#.##");

                // User should drink this amount in liter
                dbUserLiterGoal = dataSnapshot.child("myWater").getValue(Integer.class);
                userLiterGoalLiter = (float) dbUserLiterGoal / 1000;
                Double userLiterGoalLiterRound = Double.valueOf(twoDForm.format(userLiterGoalLiter));

                // User drank this amount so far in ml
                dbUserLiterCurrent = dataSnapshot.child("currentWater").getValue(Integer.class);

                // Set text with bold and normal
                String normalText = String.valueOf(" from " + userLiterGoalLiterRound + " liter");
                String boldText = String.valueOf(dbUserLiterCurrent + "ml");
                SpannableString str = new SpannableString(boldText + normalText);
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                userLiterGoal.setText(str);

                // Percent
                yourWaterPercentFloat = (float) dbUserLiterCurrent / dbUserLiterGoal *100;
                yourWaterPercent = Math.round(yourWaterPercentFloat);
                yourWater.setText(String.valueOf(yourWaterPercent + "%"));

                // Change BG from white to blue with gradient
                if(yourWaterPercent <= 10){
                    v.setBackground(getResources().getDrawable(R.drawable.my_gradient_white, null));
                }else if (yourWaterPercent <= 60) {
                    v.setBackground(getResources().getDrawable(R.drawable.my_gradient_white_white_blue, null));
                } else if (yourWaterPercent <= 100) {
                    v.setBackground(getResources().getDrawable(R.drawable.my_gradient_white_blue_blue, null));
                }else {
                    v.setBackground(getResources().getDrawable(R.drawable.my_gradient_blue, null));
                }

                // Formula for Donation
                valueDonatedToday = dataSnapshot.child("DonatedToday").getValue(Boolean.class);
                if (!valueDonatedToday) {
                    missingWater = dbUserLiterGoal - dbUserLiterCurrent;
                    Float x = (float)Math.round(missingWater ) / 1000;
                    Double roundetMissingWater = Double.valueOf(twoDForm.format(x));

                    dbDailyDonation = DataRef.child("DailyDonation");
                    dbDailyDonation.setValue(roundetMissingWater);
                }

                valueDailyDonation = dataSnapshot.child("DailyDonation").getValue(Float.class);

                // reachGoalOrDonate TextView
                if (dbUserLiterCurrent < dbUserLiterGoal){
                    reachGoalOrDonate.setText(String.valueOf("Reach your goal or donate " + valueDailyDonation +"$"));
                } else {
                    reachGoalOrDonate.setText(String.valueOf("Great! You reached your goal today!"));
                }
                if (valueDonatedToday && dbUserLiterCurrent < dbUserLiterGoal){
                    reachGoalOrDonate.setText(String.valueOf("You donated today, but don't forget to drink!"));
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
