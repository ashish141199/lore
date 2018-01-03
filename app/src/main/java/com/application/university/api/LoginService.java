package com.application.university.api;

import com.application.university.models.SmallModels;
import com.application.university.models.Pupil;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ashish on 11/12/17.
 */

public interface LoginService {
    @POST("api/users/login/")
    Call<Pupil> loginPupil(@Body Pupil pupil);

    @POST("api/users/sign_up/")
    Call<Pupil> signUpPupil(@Body Pupil pupil);

    @POST("api/fb/user/login_or_sign_up/")
    Call<Pupil> signUpOrLoginFBPupil(@Body Pupil pupil);

    @POST("api/auth/token/")
    Call<SmallModels.TokenResponse> getJWTToken(@Body Pupil pupil);

    @POST("api/fb/user/check/")
    Call<ResponseBody> checkIfNewFbPupil(@Body Pupil pupil);
}

