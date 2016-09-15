package com.harryio.orainteractive.rest;

import com.harryio.orainteractive.ui.auth.login.LoginRequest;
import com.harryio.orainteractive.ui.auth.login.LoginResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface OraService {
    @POST("users/login")
    Observable<LoginResponse> login(@Body LoginRequest loginRequest);
}
