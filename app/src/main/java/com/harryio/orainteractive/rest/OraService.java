package com.harryio.orainteractive.rest;

import com.harryio.orainteractive.ui.auth.AuthResponse;
import com.harryio.orainteractive.ui.auth.login.LoginRequest;
import com.harryio.orainteractive.ui.auth.register.RegisterRequest;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface OraService {
    @POST("users/login")
    Observable<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("users/register")
    Observable<AuthResponse> register(@Body RegisterRequest registerRequest);

    @GET("users/me")
    Observable<AuthResponse> viewProfile(@Header("Authorization") String authorization);
}
