package com.buchmaier.jacqueline.mamiwata;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;


public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private SeekBar seekBar;
    private TextView textView;
    public EditText weight;

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
        String myweight = weight.getText().toString();
        Log.d("EditText Weight", "Value: " + myweight);

        // Switch Sport
        Switch sport = v.findViewById(R.id.sportUser);
        Log.d("Switch Sport", "Value: " + sport.isChecked());

        // SeekBar Water
        seekBar = v.findViewById(R.id.seekBarWater);
        textView = v.findViewById(R.id.textSeekbar);
        textView.setText(seekBar.getProgress() + " ml");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
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
        Log.d("Switch Notifications", "Value: " + notifications.isChecked());


        return v;
    }

    // Gender Selected
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Object gender = parent.getItemAtPosition(pos);
        Log.d("ADebugTag", "Value: " + gender);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
