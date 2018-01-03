package com.application.university;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.application.university.Misc.ErrorUtils;
import com.application.university.Misc.Methods;
import com.application.university.Misc.ServiceGenerator;
import com.application.university.api.LoginService;
import com.application.university.api.UserService;
import com.application.university.models.APIError;
import com.application.university.models.Pupil;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileCreationActivity extends AppCompatActivity {
    private EditText bioEditText;
    private EditText emailEditText;
    private EditText fullNameEditText;
    private CoordinatorLayout coordinator;
    private ProgressBar progressBar;
    private Boolean fbPupilBoolean;
    private Pupil newFbPupil;
    private Pupil prefsPupilModel;
    // Progress Dialog Creator
    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        getReferences();
        getPrefsPupilModel();
        insertValuesFromPupilModel();

    }

    //gets all the references to the views
    private void getReferences() {
        progressBar = (ProgressBar) findViewById(R.id.profile_creation_progress_bar);
        emailEditText = (EditText) findViewById(R.id.profile_creation_email);
        fullNameEditText = (EditText) findViewById(R.id.profile_creation_full_name);
        bioEditText = (EditText) findViewById(R.id.profile_creation_bio);
        coordinator = (CoordinatorLayout) findViewById(R.id.profile_creation_coordinator);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //gets user model from shared prefs
    private void getPrefsPupilModel() {
        prefsPupilModel = Methods.getPupilModel(getApplicationContext());
    }

    //gets values from the current user model and displays inside the displayed fields
    private void insertValuesFromPupilModel() {
        emailEditText.setText(prefsPupilModel.getEmail());
        fullNameEditText.setText(prefsPupilModel.getFullName());
    }

    //next button click action
    public void next(View view) {
        String bioText = bioEditText.getText().toString();
        showProgress();
        updatePupilBio(bioText);
    }

    //updates the user bio on server and stores the returned model in sharedprefs
    private void updatePupilBio(String bio) {
        prefsPupilModel.setBio(bio);
        String token = prefsPupilModel.getAuthToken();
        UserService userService = ServiceGenerator.createService(UserService.class, prefsPupilModel.getEmail(), token);
        Call<Pupil> call = userService.updateBio(prefsPupilModel);

        call.enqueue(new Callback<Pupil>() {
            @Override
            public void onResponse(Call<Pupil> call, Response<Pupil> response) {
                if (response.isSuccessful()) {
                    Pupil updatedPupil = response.body();
//                    Toast.makeText(ProfileCreationActivity.this, "Bio: " + updatedPupil.getBio().toString(), Toast.LENGTH_SHORT).show();
                    storePupilDetails(response);
                    hideProgress();
                    Methods.goToHomeActivity(getApplicationContext(), true);
                    finish();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(ProfileCreationActivity.this, "Bio update error: " + error.message(), Toast.LENGTH_SHORT).show();
                    hideProgress();
                }

            }

            @Override
            public void onFailure(Call<Pupil> call, Throwable t) {
                Snackbar.make(coordinator, t.getMessage(), Snackbar.LENGTH_LONG).show();
                hideProgress();

            }
        });
    }

    //stores passed user details to SharedPreferences
    private void storePupilDetails(Response<Pupil> response) {
        Pupil user = response.body();
        Gson json = new Gson();
        String jsonPupil = json.toJson(user);
        SharedPreferences.Editor sharedPreferencesEditor = getApplicationContext().getSharedPreferences("LorePrefs", Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("currentPupil", jsonPupil);
        sharedPreferencesEditor.putBoolean("isLoggedIn", true);
        sharedPreferencesEditor.apply();
    }

    //combined function for showing all kinds of progress
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        showProgressDialog();
    }

    //combined function for hiding all kinds of progress
    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideProgressDialog();
    }

    //shows progress dialog box
    private void showProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        b.show();
    }

    //hides progress dialog box
    private void hideProgressDialog(){

        b.dismiss();
    }

//    //disabling back button
    @Override
    public void onBackPressed() {

    }


}
