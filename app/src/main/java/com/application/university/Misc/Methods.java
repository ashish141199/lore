package com.application.university.Misc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.application.university.CreateSessionActivity;
import com.application.university.HomeActivity;
import com.application.university.LoginActivity;
import com.application.university.ProfileCreationActivity;
import com.application.university.R;
import com.application.university.SignUpActivity;
import com.application.university.models.Pupil;
import com.application.university.models.Pupil;
import com.google.gson.Gson;

/**
 * Created by ashish on 25/11/17.
 */

public class Methods {
    // Progress Dialog Creator
    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;

    // Intent Methods
    // Switch Intent to LoginActivity
    public static void goToLoginActivity(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }

    // Switch Intent to HomeActivity
    public static void goToHomeActivity(Context context, Boolean finish) {

        Intent i = new Intent(context, HomeActivity.class);
        if (finish) {
            context.startActivity(i);

        } else {
            context.startActivity(i);
        }
    }

    //Switch Activity to ProfileCreationActivity
    public static void goToProfileCreationActivity(Context context) {
        Intent i = new Intent(context, ProfileCreationActivity.class);
        context.startActivity(i);

    }

    //Switch Activity to SignUpActivity
    public static void goToSignUpActivity(Context context) {
        Intent i = new Intent(context, SignUpActivity.class);
        context.startActivity(i);
    }
//
//    //retrieves user model from sharedprefs and returns it
//    public static User geUserModel(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("LorePrefs", Context.MODE_PRIVATE);
//        String json = sharedPreferences.getString("currentPupil", "None");
//        if (json.equals("None")) {
//            return null;
//        }
//        Gson gson = new Gson();
//        User user = gson.fromJson(json, User.class);
//        return user;
//    }

    public static Pupil getPupilModel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LorePrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("currentPupil", "None");
        if (json.equals("None")) {
            return null;
        }
        Gson gson = new Gson();
        Pupil pupil = gson.fromJson(json, Pupil.class);
        return pupil;
    }

    public static void logout(Context context) {
        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences("LorePrefs", Context.MODE_PRIVATE).edit();
        sharedPreferences.putBoolean("isLoggedIn", false);
        sharedPreferences.remove("currentPupil");
        sharedPreferences.apply();
        goToLoginActivity(context);
    }

    //Method to hide softkeyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Redirects to createsessionactivity
    public static void goToCreateSessionActivity(Context context) {
        Intent i = new Intent(context, CreateSessionActivity.class);
        context.startActivity(i);
    }


}
