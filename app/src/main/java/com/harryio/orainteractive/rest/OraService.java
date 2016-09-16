package com.harryio.orainteractive.rest;

import com.harryio.orainteractive.ui.auth.AuthResponse;
import com.harryio.orainteractive.ui.auth.login.LoginRequest;
import com.harryio.orainteractive.ui.auth.register.RegisterRequest;
import com.harryio.orainteractive.ui.chat.Chat;
import com.harryio.orainteractive.ui.chat.ChatList;
import com.harryio.orainteractive.ui.chat.CreateChatRequest;
import com.harryio.orainteractive.ui.chat.CreateMessageRequest;
import com.harryio.orainteractive.ui.chat.CreateMessageResponse;
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

/**
 * Class representing REST api calls as defined <a href="challenge.orainteractive.com">here</a>
 */
public interface OraService {
    /**
     * Login existing user
     *
     * @param loginRequest Object representing json schema as expected for the login api
     * @return {@link rx.Observable} which will emit the response
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/login">http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/login</a>
     */
    @POST("users/login")
    Observable<AuthResponse> login(@Body LoginRequest loginRequest);

    /**
     * Register new user
     * @param registerRequest Object representing json schema as expected for the register api
     * @return {@link rx.Observable} which will emit the response
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/register">http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/register</a>
     */
    @POST("users/register")
    Observable<AuthResponse> register(@Body RegisterRequest registerRequest);

    /**
     * View user's profile
     * @param authorization Authorization token
     * @return {@link rx.Observable} which will emit information about user's profile
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/view">http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/view</a>
     */
    @GET("users/me")
    Observable<AuthResponse> viewProfile(@Header("Authorization") String authorization);

    /**
     * Edit user's profile
     * @param authorization Authorization token
     * @param request Object representing json schema as expected for the edit profile api
     * @return {@link rx.Observable} which will emit the response
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/edit">http://docs.oracodechallenge.apiary.io/#reference/endpoints/user/edit</a>
     */
    @PUT("users/me")
    Observable<AuthResponse> editProfile(@Header("Authorization") String authorization,
                                         @Body EditProfileRequest request);

    /**
     * Get a list of chats related to the user
     * @param authorization Authorization token
     * @param query A search query for the name
     * @param page Page of the list
     * @param limit Limit per page
     * @return {@link rx.Observable} which will emit a list of chats
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/chat/list">http://docs.oracodechallenge.apiary.io/#reference/endpoints/chat/list</a>
     */
    @GET("chats")
    Observable<ChatList> getChatList(@Header("Authorization") String authorization,
                                     @Query("q") String query, @Query("page") String page,
                                     @Query("limit") Number limit);

    /**
     * Create a new chat
     * @param authorization Authorization token
     * @param request Object representing json schema as expected for the create chat api
     * @return {@link rx.Observable} which will emit the newly created chat if the call is successful
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/chat/create">http://docs.oracodechallenge.apiary.io/#reference/endpoints/chat/create</a>
     */
    @POST("chats")
    Observable<Chat> createChat(@Header("Authorization") String authorization,
                                @Body CreateChatRequest request);

    /**
     * Get list of messages related to chat
     * @param authorization Authorization token
     * @param chatId Id of the chat for which messages are to be retrieved
     * @param page Page of list
     * @param limit Limit per page
     * @return {@link rx.Observable} which will emit a list of messages for the chat
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/messages/list>http://docs.oracodechallenge.apiary.io/#reference/endpoints/messages/list</a>"
     */
    @GET("chats/{chat_id}/messages")
    Observable<MessageList> getMessageList(@Header("Authorization") String authorization,
                                           @Path("chat_id") String chatId, @Query("page") String page,
                                           @Query("limit") Number limit);

    /**
     * Create a new message in the chat
     * @param authorization Authorization token
     * @param chatId Id of the chat in which message is to be created
     * @param request Object representing json schema as expected for the create message api
     * @return {@link rx.Observable} which will emit the newly created message if the call is successful
     * @see <a href="http://docs.oracodechallenge.apiary.io/#reference/endpoints/messages/create">http://docs.oracodechallenge.apiary.io/#reference/endpoints/messages/create</a>
     */
    @POST("chats/{chat_id}/messages")
    Observable<CreateMessageResponse> createMessage(@Header("Authorization") String authorization,
                                                    @Path("chat_id") String chatId, @Body CreateMessageRequest request);
}
