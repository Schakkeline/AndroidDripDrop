package com.buchmaier.jacqueline.mamiwata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    // SeekBar for user input water
    private SeekBar seekBar;
    private TextView textView;

    // Sport switch
    Switch sport;
    Boolean sporti;
    Float userSport = Float.valueOf(String.valueOf(0));

    // Notification switch
    Switch notifications;
    Boolean notis;

    int progress;

    // Water the user should drink
    Integer myWater;

    // Weight
    public EditText weight;
    Integer userInputWeight;
    Integer value;

    // Age
    Object age;
    String userInputAge;
    Spinner spinnerAge;
    ArrayAdapter<String> adapter;
    String userAge;
    Float userAgeSpinner;

    // Read from the database
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = firebaseUser.getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

    DatabaseReference dbWeight = db.child("weight");
    DatabaseReference dbAge = db.child("age");
    DatabaseReference dbSport = db.child("sport");
    DatabaseReference dbMyWater = db.child("myWater");
    DatabaseReference dbNotifications = db.child("notifications");
    FirebaseAuth auth;
    Button signOut;
    Button about;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Sign out
        auth = FirebaseAuth.getInstance();
        signOut = v.findViewById(R.id.sign_out);

        // About
        about = v.findViewById(R.id.about);

        // Spinner Age
        String [] valuesAge = {"under 30 years","between 30 and 55 years","over 55 years",};
        spinnerAge = v.findViewById(R.id.ageUser);
        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, valuesAge);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerAge.setAdapter(adapter);
        spinnerAge.setOnItemSelectedListener(this);

        // EditText Weight
        weight   = v.findViewById(R.id.weightUser);
        weight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    userInputWeight = Integer.parseInt(v.getText().toString());
                    dbWeight.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dbWeight.setValue(userInputWeight);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.w("Error", "Failed to read value.", error.toException());
                        }
                    });
                    return true;
                }
                return false;
            }
        });


        // SeekBar Water
        seekBar = v.findViewById(R.id.seekBarWater);
        textView = v.findViewById(R.id.textSeekbar);
        textView.setText(seekBar.getProgress() + " ml");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                textView.setText(seekBar.getProgress() + " ml");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText(seekBar.getProgress() + " ml");
                // TODO: Set user input from SeekBar as water
                dbMyWater.setValue(seekBar.getProgress());
            }
        });

        // Switch Notifications
        notifications = v.findViewById(R.id.notification_switch);
        // Set Notis switch from database - default is true
        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dbNotifications.setValue(true);
                }
                else {
                    dbNotifications.setValue(false);
                }
            }
        });

        // Switch Sport
        sport = v.findViewById(R.id.sportUser);
        sport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dbSport.setValue(true);
                    userSport = Float.valueOf(String.valueOf(500));
                }
                else {
                    dbSport.setValue(false);
                    userSport = Float.valueOf(String.valueOf(0));
                }
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Weight from Database
                value = dataSnapshot.child("weight").getValue(Integer.class);
                weight.setText(String.valueOf(value));

                // Get Age from Database
                userAge = dataSnapshot.child("age").getValue(String.class);
                ArrayAdapter myAdap = (ArrayAdapter) spinnerAge.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition = myAdap.getPosition(userAge);
                spinnerAge.setSelection(spinnerPosition);

                // "under 30 years","between 30 and 55 years","over 55 years"
                switch (userAge) {
                    case "under 30 years":
                        userAgeSpinner = Float.valueOf(String.valueOf(40));
                        break;
                    case "between 30 and 55 years":
                        userAgeSpinner = Float.valueOf(String.valueOf(35));
                        break;
                    case "over 55 years":
                        userAgeSpinner = Float.valueOf(String.valueOf(30));
                        break;
                }

                // Set Sport switch from database - and add extra water if necessary
                sporti = dataSnapshot.child("sport").getValue(Boolean.class);
                sport.setChecked(sporti);

                // Set Notification switch from database
                notis = dataSnapshot.child("notifications").getValue(Boolean.class);
                notifications.setChecked(notis);

                // My Water
                // Formula: weight * age / 28.3 * 0.03
                // if sport - add 500ml
                // age: under 30 -> 40
                // age: between 30 and 55 -> 35
                // age: over 55 -> 30
                Float w = Float.valueOf(String.valueOf(value));
                Float z = Float.valueOf(String.valueOf(28.3));
                Float n = Float.valueOf(String.valueOf(0.03));
                myWater =  Math.round(w * userAgeSpinner / z * n * 1000 + userSport);

                // Set the Water on screen
                textView.setText(String.valueOf(myWater + "ml"));
                // save to database
                dbMyWater.setValue(myWater);
                // set seekbar
                seekBar.setProgress(myWater);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), About.class));
            }
        });

        return v;
    }

    // Gender Selected
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        age = parent.getItemAtPosition(pos);
        userInputAge = age.toString();

        // Set userGender in database
        dbAge.setValue(userInputAge);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //sign out method
    public void signOut() {
        auth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

}
