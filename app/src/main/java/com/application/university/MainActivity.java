package com.application.university;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.application.university.Misc.Methods;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogIn();

    }

    private void checkLogIn() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LorePrefs", Context.MODE_PRIVATE);
        Boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Methods.goToHomeActivity(this,false);
            finish();
        } else {
            Methods.goToLoginActivity(this);
            finish();
        }
    }
}
