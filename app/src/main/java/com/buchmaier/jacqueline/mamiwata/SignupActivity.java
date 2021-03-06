package com.buchmaier.jacqueline.mamiwata;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private FirebaseAuth auth;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "Welcome :)", Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed :(",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Set dummy data for myWater with dummy user data
                                    Float w = Float.valueOf(String.valueOf(50.0));
                                    Float z = Float.valueOf(String.valueOf(28.3));
                                    Float n = Float.valueOf(String.valueOf(0.03));
                                    Float t = Float.valueOf(String.valueOf(1000.0));
                                    Float u = Float.valueOf(String.valueOf(40.0));
                                    Integer myWater =  Math.round(w * u / z * n * t);

                                    // User UID is used for user database key
                                    // Setup user data for first use
                                    String uid = auth.getUid();
                                    mFirebaseDatabase.child(uid).child("email").setValue(email);
                                    mFirebaseDatabase.child(uid).child("age").setValue("under 30 years");
                                    mFirebaseDatabase.child(uid).child("currentWater").setValue(0);
                                    mFirebaseDatabase.child(uid).child("DailyDonation").setValue(0);
                                    mFirebaseDatabase.child(uid).child("DonatedToday").setValue(false);
                                    mFirebaseDatabase.child(uid).child("myWater").setValue(myWater);
                                    mFirebaseDatabase.child(uid).child("notifications").setValue(true);
                                    mFirebaseDatabase.child(uid).child("sport").setValue(false);
                                    mFirebaseDatabase.child(uid).child("weight").setValue(50);
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
