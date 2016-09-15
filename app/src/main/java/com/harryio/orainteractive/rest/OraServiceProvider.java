package com.harryio.orainteractive.rest;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class OraServiceProvider {
    private static final String BASE_URL = "http://private-d9e5b-oracodechallenge.apiary-mock.com/";

    private static OraService instance;

    private OraServiceProvider() {
    }

    public static OraService getInstance() {
        if (instance == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Headers headers = request.headers().newBuilder()
                                    .add("Content-Type", "application/json; charset=utf-8")
                                    .add("Accept", "application/json")
                                    .build();
                            request = request.newBuilder().headers(headers).build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();

            instance = retrofit.create(OraService.class);
        }

        return instance;
    }
}
