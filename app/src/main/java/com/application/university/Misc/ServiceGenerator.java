package com.application.university.Misc;

import android.text.TextUtils;
import android.util.Base64;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ashish on 11/12/17.
 */

public class ServiceGenerator {

    public static final String API_BASE_URL = "http://192.168.1.109:8000/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(
            Class<S> serviceClass, String email, String authToken) {
        if (!TextUtils.isEmpty(authToken)) {

            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(email, authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}
