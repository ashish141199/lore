package com.application.university.Misc;

import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ashish on 25/12/17.
*/

public class AuthenticationInterceptor implements Interceptor {
    private String authToken;
    private String email;

    public AuthenticationInterceptor(String email, String token) {
        this.authToken = token;
        this.email = email;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder().header("Authorization", authToken).header("Email", email);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
