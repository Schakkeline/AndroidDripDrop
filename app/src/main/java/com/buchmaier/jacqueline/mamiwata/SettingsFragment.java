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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private SeekBar seekBar;
    private TextView textView;
    
    Boolean sporti;
    int progress;
    Boolean notis;

    public EditText weight;
    Integer userInputWeight;
    Object gender;

    // Read from the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db = database.getReference("settings");
    DatabaseReference dbWeight = database.getReference("settings").child("weight");
    DatabaseReference dbGender = database.getReference("settings").child("gender");

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Spinner Gender
        String [] valuesGender = {"Female","Male",};
        Spinner spinnerGender = v.findViewById(R.id.genderUser);
        ArrayAdapter<String> adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, valuesGender);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setOnItemSelectedListener(this);

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


        // Switch Sport
        Switch sport = v.findViewById(R.id.sportUser);
        sporti = sport.isChecked();
        Log.d("Switch Sport", "Value: " + sporti);

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
        Switch notifications = v.findViewById(R.id.notification_switch);
        notis = notifications.isChecked();
        Log.d("Switch Notifications", "Value: " + notis);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.child("weight").getValue(Integer.class);
                String value2 = dataSnapshot.child("gender").getValue(String.class);
                weight.setText(String.valueOf(value));
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
        gender = parent.getItemAtPosition(pos);
        Log.d("ADebugTag", "Value: " + gender);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
