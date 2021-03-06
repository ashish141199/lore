package com.application.university;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.university.Misc.ErrorUtils;
import com.application.university.Misc.Methods;
import com.application.university.Misc.ServiceGenerator;
import com.application.university.api.LoginService;
import com.application.university.models.APIError;
import com.application.university.models.SmallModels;
import com.application.university.models.Pupil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText signUp_email, signUp_password, signUp_password_again, signUp_full_name;
    private CoordinatorLayout coordinatorLayout;
    private Pupil tempPupil;
    private Button signUp_button;
    private AccessToken accessToken;
    private LoginButton loginButton;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    // Progress Dialog Creator
    private AlertDialog b;
    private AlertDialog.Builder dialogBuilder;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up);
        getReferences();


    }
    //onclick action for signup button

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
        String s1 = signUp_email.getText().toString(), s2 = signUp_full_name.getText().toString(), s3 = signUp_password.getText().toString(), s4 = signUp_password_again.getText().toString();

        if(s1.length() > 0 && s2.length() > 0 && s3.length() > 0 && s4.length() > 0)
        {
            signUp_button.setEnabled(true);
        }

        else{
            signUp_button.setEnabled(false);
        }
    }

    //onclick function for signupbutton click
    public void signUp(View view) {
        //hides keyboard so snackbar would be visible
        Methods.hideKeyboard(this);
        // Gets all texts and stores in variables
        String email = signUp_email.getText().toString();
        String fullName = signUp_full_name.getText().toString();
        String password = signUp_password.getText().toString();
        String passwordAgain = signUp_password_again.getText().toString();

        
        // Checks if password and retype passwords match. If does proceed, if doesn't display error and return
        if (!password.matches(passwordAgain)) {
            signUp_password_again.setError("Passwords do not match.");
            return;
        }

        // Checks if password length is too short

        if (password.length() < 8) {
            signUp_password.setError("Password length must be 8 characters or more");
            signUp_password.requestFocus();
            return;
        }
        //Email Check
        if (!isValidEmail(signUp_email.getText())) {
            signUp_email.setError("Not a valid email address.");
            return;
        }
        //Create temp pupil model and stores all values, Login Service sign up create service
        tempPupil = new Pupil();
        tempPupil.setEmail(email);
        tempPupil.setFullName(fullName);
        tempPupil.setPassword(password);

        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<Pupil> call = loginService.signUpPupil(tempPupil);
        // Send Call to server
        showProgressDialog();

        call.enqueue(new Callback<Pupil>() {
            @Override
            public void onResponse(Call<Pupil> call, Response<Pupil> response) {
                if (response.isSuccessful()) {
                    // If successful, store in sharedprefs as loggedInPupil and redirect to ProfileCreationActivity
                    storePupilDetails(response);
//                    getJWTToken(response.body().getEmail(), tempPupil.getPassword());
                    Methods.goToProfileCreationActivity(SignUpActivity.this);
                    hideProgressDialog();
                    finish();




                } else {
                    // If error, display message, do nothing
                    hideProgressDialog();
                    APIError error = ErrorUtils.parseError(response);
                    signUpErrorHandling(error);
                }
            }

            @Override
            public void onFailure(Call<Pupil> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });



        
    }

    //gets references to all views on the layout
    public void getReferences() {
        signUp_email = (EditText) findViewById(R.id.signUp_email);
        signUp_full_name = (EditText) findViewById(R.id.signUp_fullName);
        signUp_password = (EditText) findViewById(R.id.signUp_password);
        signUp_password_again = (EditText) findViewById(R.id.signUp_passwordAgain);
        signUp_button = (Button) findViewById(R.id.signUp_button);

        signUp_email.addTextChangedListener(textWatcher);
        signUp_full_name.addTextChangedListener(textWatcher);
        signUp_password.addTextChangedListener(textWatcher);
        signUp_password_again.addTextChangedListener(textWatcher);

//        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
//        avi.hide();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        checkEmptyFieldsForButtonDisablity();

        loginButton = (LoginButton) findViewById(R.id.sign_up_activity_fb_login_button);
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
        showProgressDialog();
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
                hideProgressDialog();
                checkIfNewFbPupil_errorHandling(error);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressDialog();
                Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG).show();
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
                        hideProgressDialog();
                        Snackbar.make(coordinatorLayout, "Welcome Back to Lore", Snackbar.LENGTH_LONG).show();
                        Methods.goToHomeActivity(getApplicationContext(), true);
                        finish();
                    } else {
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

    //stores pupil details to sharedprefs
    private void storePupilDetails(Response<Pupil> response) {
        Pupil pupil = response.body();
        Gson json = new Gson();
        String jsonPupil = json.toJson(pupil);
        SharedPreferences.Editor sharedPreferencesEditor = getApplicationContext().getSharedPreferences("LorePrefs", Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("currentPupil", jsonPupil);
        sharedPreferencesEditor.putBoolean("isLoggedIn", true);
        sharedPreferencesEditor.apply();
    }

    //handles error according to their status code
    private void signUpErrorHandling(APIError error) {
        String message = error.message();
        Integer status = error.status();
        // email already exists
        if (status == 409) {
            signUp_email.setError("Account with Email already exists");
            signUp_email.requestFocus();
        }
        if (status == 411) {
            signUp_password.setError(message);
            signUp_password.requestFocus();
        }
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
        showProgressDialog();
        call.enqueue(new Callback<SmallModels.TokenResponse>() {
            @Override
            public void onResponse(Call<SmallModels.TokenResponse> call, Response<SmallModels.TokenResponse> response) {
                SmallModels.TokenResponse tokenResponse = response.body();

                String token = tokenResponse.getToken();
                if (token.equals("")) {
                    Snackbar.make(coordinatorLayout, "There seems to be some Error", Snackbar.LENGTH_LONG).show();
                }
                Pupil pupil = Methods.getPupilModel(SignUpActivity.this);
                pupil.setAuthToken(token);
                Gson gson = new Gson();
                String json = gson.toJson(pupil);
                SharedPreferences.Editor sharedPreferencesEditor = getApplicationContext().getSharedPreferences("LorePrefs", Context.MODE_PRIVATE).edit();
                sharedPreferencesEditor.putString("currentPupil", json);
                sharedPreferencesEditor.putBoolean("isLoggedIn", true);
                sharedPreferencesEditor.apply();
                hideProgressDialog();



            }

            @Override
            public void onFailure(Call<SmallModels.TokenResponse> call, Throwable t) {
                hideProgressDialog();
                Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    //redirects to log in activity
    public void goToLogIn(View view) {
        Methods.goToLoginActivity(getApplicationContext());
        finish();
    }

    //shows progress dialog box
    private void showProgressDialog() {
//            avi.smoothToShow();
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        b.show();
    }

    //hides progress dialog box
    private void hideProgressDialog(){
//
        b.dismiss();
//        avi.smoothToHide();
    }

    //necessary for fb sdk to work
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Email Validator
    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
