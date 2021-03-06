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
import android.view.View.OnClickListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class BottomSheetFragment extends BottomSheetDialogFragment implements OnClickListener{

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = firebaseUser.getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
    DatabaseReference dbCurrentWater = db.child("currentWater");

    Integer addDrink;

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
        final View v = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

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
                addDrink = 100;
                break;
            case R.id.secondML:
                addDrink = 250;
                break;
            case R.id.thirdML:
                addDrink = 330;
                break;
            case R.id.fourthML:
                addDrink = 500;
                break;
            case R.id.fifthML:
                addDrink = 1000;
                break;
        }

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer valueCurrentWater = dataSnapshot.child("currentWater").getValue(Integer.class);
                dbCurrentWater.setValue(valueCurrentWater + addDrink);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // throw an error if setValue() is rejected
                throw databaseError.toException();
            }
        });

    }

}
