package com.application.university;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.university.Misc.ErrorUtils;
import com.application.university.Misc.Methods;
import com.application.university.Misc.ServiceGenerator;
import com.application.university.api.LoginService;
import com.application.university.models.APIError;
import com.application.university.models.Pupil;
import com.application.university.models.SmallModels;
import com.application.university.models.Pupil;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.stetho.inspector.network.ResponseBodyData;
import com.facebook.stetho.inspector.protocol.module.Database;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;
    private CoordinatorLayout coordinatorLayout;
    private LoginButton loginButton;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private boolean loggedIn;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    // Progress Dialog Creator
    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;
    private Button pupilLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        getReferences();
        checkEmptyFieldsForButtonDisablity();
    }

    //TextWatcher for checking empty fields everytime the pupil enters something
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkEmptyFieldsForButtonDisablity();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    //only enables buttons when both fields are filled
    private void checkEmptyFieldsForButtonDisablity() {
        String s1 = emailTextView.getText().toString(), s2 = passwordTextView.getText().toString();
        if(s1.equals("") && s2.equals(""))
        {
            pupilLoginButton.setEnabled(false);
        }

        else if(!s1.equals("")&&s2.equals("")){
            pupilLoginButton.setEnabled(false);
        }

        else if(!s2.equals("")&&s1.equals(""))
        {
            pupilLoginButton.setEnabled(false);
        }

        else
        {
            pupilLoginButton.setEnabled(true);
        }
    }

    //gets the references for all the XML objects
    public void getReferences() {
        emailTextView = (TextView) findViewById(R.id.login_email);
        passwordTextView = (TextView) findViewById(R.id.login_password);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_coordinator);
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        pupilLoginButton = (Button) findViewById(R.id.login_button);

        //adding textwatcher to text views
        emailTextView.addTextChangedListener(textWatcher);
        passwordTextView.addTextChangedListener(textWatcher);

        // Facebook Login Button Initialization
        // Setting permissions for accessing
        loginButton.setReadPermissions("email user_birthday");

        //setting callback actions
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                setResult(RESULT_OK);
                 GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Pupil newFbPupil = new Pupil();
                        try {
                            newFbPupil.setEmail(object.getString("email"));
                            newFbPupil.setFullName(object.getString("name"));
                            checkIfNewFbPupil(newFbPupil);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
//                checkIfNewFbPupil();
//                Methods.goToProfileCreationActivity(LoginActivity.this, true);

            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                Snackbar.make(coordinatorLayout, "Facebook Login was Cancelled", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(coordinatorLayout, error.getMessage(), Snackbar.LENGTH_LONG).show();

            }
        });


    }

    //checks if already existing fb pupil or a new pupil
    private void checkIfNewFbPupil(Pupil newFbPupil) {
        //create login service for api call
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<ResponseBody> call = loginService.checkIfNewFbPupil(newFbPupil);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                APIError error = null;
                try {
                    error = gson.fromJson(response.body().string(), APIError.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                checkIfNewFbPupil_errorHandling(error);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    //error handling for checkIfNewFbPupil function
    private void checkIfNewFbPupil_errorHandling(APIError error) {
        String message = error.message();
        int status = error.status();
        if (status == 200) {
            createOrLoginFbPupil(true);
        } else if (status == 409) {
            //Email is already taken
            Snackbar.make(coordinatorLayout, error.message(), Snackbar.LENGTH_LONG).show();
        } else if (status == 201) {
            //email is available
            createOrLoginFbPupil(false);
        }
    }

    //gets pupil details from fb graph api and sends to server to create a new FB Pupil
    private void createOrLoginFbPupil(final Boolean homeActivityRedirect) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Pupil fbPupil = new Pupil();
                try {
                    fbPupil.setEmail(object.getString("email"));
                    fbPupil.setFullName(object.getString("name"));
                    fbPupil.setFbUser(true);


                    //sends to server
                    sendFbPupilToServer(fbPupil, homeActivityRedirect);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();

        //sends to profilecreationactivity
//        Methods.goToProfileCreationActivity(getApplicationContext(), true);

    }

    //sends fbpupil details acquired from graph api to server
    private void sendFbPupilToServer(Pupil pupil, final Boolean homeActivityRedirect) {
        //creating service
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<Pupil> call = loginService.signUpOrLoginFBPupil(pupil);
        showProgressDialog();
        //calling server
        call.enqueue(new Callback<Pupil>() {
            @Override
            public void onResponse(Call<Pupil> call, Response<Pupil> response) {
                if (response.isSuccessful()) {
                    //if successful
                    //store pupil details and redirect to profile creation
                    Pupil newPupil = response.body();
                    storePupilDetails(response);

                    //check if pupil was logged in or signed up
                    if (homeActivityRedirect) {
                        //pupil was logged in. welcome back
                        //hide progress dialog
                        hideProgressDialog();
                        Snackbar.make(coordinatorLayout, "Welcome Back to Lore", Snackbar.LENGTH_LONG).show();
                        Methods.goToHomeActivity(getApplicationContext(), true);
                        finish();
                    } else {
                        //hideprogress dialog
                        hideProgressDialog();
                        Methods.goToProfileCreationActivity(getApplicationContext());
                        finish();
                    }
                } else {
                    //if error
                    hideProgressDialog();
                    APIError error = ErrorUtils.parseError(response);
                    Snackbar.make(coordinatorLayout, error.message(), Snackbar.LENGTH_LONG).show();
                    //display error
                    //handle error

                }

            }

            @Override
            public void onFailure(Call<Pupil> call, Throwable t) {
                hideProgressDialog();
                Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    //stores passed pupil details to SharedPreferences
    private void storePupilDetails(Response<Pupil> response) {
        Pupil pupil = response.body();
        Gson json = new Gson();
        String jsonPupil = json.toJson(pupil);
        SharedPreferences.Editor sharedPreferencesEditor = getApplicationContext().getSharedPreferences("LorePrefs", Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("currentPupil", jsonPupil);
        sharedPreferencesEditor.putBoolean("isLoggedIn", true);
        sharedPreferencesEditor.apply();
    }

    //    Login Button was pressed
    public void pupilLogin(View view) {
        //hides keyboard so snackbar would be visible
        Methods.hideKeyboard(this);

        String login_email = emailTextView.getText().toString();
        final String login_password = passwordTextView.getText().toString();

        //Creating a pupil with the above credentials
        Pupil pupil = new Pupil();
        pupil.setEmail(emailTextView.getText().toString());
        pupil.setPassword(passwordTextView.getText().toString());

        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<Pupil> call = loginService.loginPupil(pupil);
        showProgressDialog();
        call.enqueue(new Callback<Pupil>() {
            @Override
            public void onResponse(Call<Pupil> call, Response<Pupil> response) {
                if (response.isSuccessful()) {
                    storePupilDetails(response);
                    getJWTToken(response.body().getEmail(), login_password);
                    hideProgressDialog();
//                    Methods.goToProfileCreationActivity(getApplicationContext(), false);





                } else {
                    hideProgressDialog();
                    APIError error = ErrorUtils.parseError(response);
                    Snackbar.make(coordinatorLayout, error.message(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Pupil> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                snackbarErrorHandling(t);

            }
        });

    }

    //gets jwt token from django server and stores in pupil model
    private void getJWTToken(String email, String password) {
        //creating temporary pupil
        Pupil tempPupil = new Pupil();
        tempPupil.setEmail(email);
        tempPupil.setPassword(password);

        //sending temp pupil to server
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<SmallModels.TokenResponse> call = loginService.getJWTToken(tempPupil);

        call.enqueue(new Callback<SmallModels.TokenResponse>() {
            @Override
            public void onResponse(Call<SmallModels.TokenResponse> call, Response<SmallModels.TokenResponse> response) {
                if (response.isSuccessful()) {

                    SmallModels.TokenResponse tokenResponse = response.body();


                    String token = tokenResponse.getToken();
                    if (token.equals("")) {
                        Snackbar.make(coordinatorLayout, "There seems to be some Error", Snackbar.LENGTH_LONG).show();
                    }
                    Pupil pupil = Methods.getPupilModel(LoginActivity.this);
                    pupil.setAuthToken(token);
                    Gson gson = new Gson();
                    String json = gson.toJson(pupil);
                    SharedPreferences.Editor sharedPreferencesEditor = getApplicationContext().getSharedPreferences("LorePrefs", Context.MODE_PRIVATE).edit();
                    sharedPreferencesEditor.putString("currentPupil", json);
                    sharedPreferencesEditor.putBoolean("isLoggedIn", true);
                    sharedPreferencesEditor.apply();
                    Methods.goToHomeActivity(LoginActivity.this, true);
                    finish();

                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(LoginActivity.this, "JWT Error: " + error.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<SmallModels.TokenResponse> call, Throwable t) {
                Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    //redirects to sign up activity
    public void goToSignUp(View view) {
        Methods.goToSignUpActivity(this);
    }

    //handles login snackbar error handling
    private void snackbarErrorHandling(Throwable t) {
        if (t.getMessage().contains("connect")) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Failed to connect to server.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
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

    //necessary for fb sdk to work
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
