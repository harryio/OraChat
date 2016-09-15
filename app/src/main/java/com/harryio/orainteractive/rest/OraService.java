package com.harryio.orainteractive.rest;

import com.harryio.orainteractive.ui.account.EditProfileRequest;
import com.harryio.orainteractive.ui.auth.AuthResponse;
import com.harryio.orainteractive.ui.auth.login.LoginRequest;
import com.harryio.orainteractive.ui.auth.register.RegisterRequest;
import com.harryio.orainteractive.ui.chat.Chat;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

public interface OraService {
    @POST("users/login")
    Observable<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("users/register")
    Observable<AuthResponse> register(@Body RegisterRequest registerRequest);

    @GET("users/me")
    Observable<AuthResponse> viewProfile(@Header("Authorization") String authorization);

    @PUT("users/me")
    Observable<AuthResponse> editProfile(@Header("Authorization") String authorization,
                                         @Body EditProfileRequest request);

    @GET("chats")
    Observable<Chat> getChatList(@Header("Authorization") String authorization,
                                 @Query("q") String query, @Query("page") String page,
                                 @Query("limit") Number limit);
}
