package com.harryio.orainteractive.rest;

import com.harryio.orainteractive.ui.auth.AuthResponse;
import com.harryio.orainteractive.ui.auth.login.LoginRequest;
import com.harryio.orainteractive.ui.auth.register.RegisterRequest;
import com.harryio.orainteractive.ui.chat.Chat;
import com.harryio.orainteractive.ui.chat.ChatList;
import com.harryio.orainteractive.ui.chat.CreateChatRequest;
import com.harryio.orainteractive.ui.chat.MessageList;
import com.harryio.orainteractive.ui.profile.EditProfileRequest;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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
    Observable<ChatList> getChatList(@Header("Authorization") String authorization,
                                     @Query("q") String query, @Query("page") String page,
                                     @Query("limit") Number limit);

    @POST("chats")
    Observable<Chat> createChat(@Header("Authorization") String authorization,
                                @Body CreateChatRequest request);

    @GET("chats/{chat_id}/messages")
    Observable<MessageList> getMessageList(@Header("Authorization") String authorization,
                                           @Path("chat_id") String chatId, @Query("page") String page,
                                           @Query("limit") Number limit);
}
