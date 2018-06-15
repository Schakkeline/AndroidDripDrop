package com.buchmaier.jacqueline.mamiwata;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by jacqueline on 01.06.18.
 * TODO: Implement User Model
 */

@IgnoreExtraProperties
public class User {
    public String name;
    public String email;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}