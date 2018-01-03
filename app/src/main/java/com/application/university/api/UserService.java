package com.application.university.api;

import com.application.university.models.Pupil;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by ashish on 25/12/17.
 */

public interface UserService {

    @POST("api/users/details/bio/update/")
    Call<Pupil> updateBio(@Body Pupil pupil);

}
