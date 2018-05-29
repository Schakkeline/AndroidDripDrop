package com.buchmaier.jacqueline.mamiwata;

/**
 * Created by jacqueline on 14.05.18.
 */

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Log;
import android.view.View.OnClickListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


public class BottomSheetFragment extends BottomSheetDialogFragment implements OnClickListener{

    private static final String TAG = "BottomSheetFragment: ";

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        //Create my buttons and set their onClickListener to "this"
        Button b1 = v.findViewById(R.id.firstML);
        b1.setOnClickListener(this);

        Button b2 = v.findViewById(R.id.secondML);
        b2.setOnClickListener(this);

        Button b3 = v.findViewById(R.id.thirdML);
        b3.setOnClickListener(this);

        Button b4 = v.findViewById(R.id.fourthML);
        b4.setOnClickListener(this);

        Button b5 = v.findViewById(R.id.fifthML);
        b5.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.firstML:
                Log.d(TAG, "Button 100ml clicked");
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue("Hello, World!");
                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                break;
            case R.id.secondML:
                Log.d(TAG, "Button 250ml clicked");
                break;
            case R.id.thirdML:
                Log.d(TAG, "Button 330ml clicked");
                break;
            case R.id.fourthML:
                Log.d(TAG, "Button 500ml clicked");
                break;
            case R.id.fifthML:
                Log.d(TAG, "Button 1000ml clicked");
                break;
        }
    }


}
