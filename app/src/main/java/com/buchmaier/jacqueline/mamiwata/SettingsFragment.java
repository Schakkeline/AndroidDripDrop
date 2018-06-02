package com.buchmaier.jacqueline.mamiwata;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private SeekBar seekBar;
    private TextView textView;

    Switch sport;
    Switch notifications;

    Boolean sporti;
    int progress;

    // Weight
    public EditText weight;
    Integer userInputWeight;

    // Age
    Object age;
    String userInputAge;
    Spinner spinnerAge;
    ArrayAdapter<String> adapter;

    // Read from the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("settings");
    DatabaseReference dbWeight = database.getReference("settings").child("weight");
    DatabaseReference dbAge = database.getReference("settings").child("age");
    DatabaseReference dbSport = database.getReference("settings").child("sport");
    DatabaseReference dbNotifications = database.getReference("settings").child("notifications");

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

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
                    Log.d("EditText Weight bal", "Value: " + v.getEditableText().toString());
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
            }
        });

        // Switch Notifications
        notifications = v.findViewById(R.id.notification_switch);
        // Default is set to true
        dbNotifications.setValue(true);

        // Switch Sport
        sport = v.findViewById(R.id.sportUser);
        // Default is set to false
        dbSport.setValue(false);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Weight from Database
                Integer value = dataSnapshot.child("weight").getValue(Integer.class);
                weight.setText(String.valueOf(value));

                // Get Gender from Database
                String value2 = dataSnapshot.child("age").getValue(String.class);
                ArrayAdapter myAdap = (ArrayAdapter) spinnerAge.getAdapter(); //cast to an ArrayAdapter
                int spinnerPosition = myAdap.getPosition(value2);
                spinnerAge.setSelection(spinnerPosition);

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

                // Set Sport switch from database - default is false
                sport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            dbSport.setValue(true);
                        }
                        else {
                            dbSport.setValue(false);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
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
        Log.d("ADebugTag", "Value Gender: " + age);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
