package com.fourbeams.testtasktinkoff;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * Generates REST service for obtaining news from {@value #API_BASE_URL}.
 * <br/> Setting up OkHttp as a Http client and GsonConverterFactory as a JSON converter.
 * <br/> Setting up reading from server timeout: {@value #READ_TIMEOUT_SECONDS}
 * <br/> Setting up connection with server timeout: {@value #CONNECT_TIMEOUT_SECONDS}
 *
 */
public class RESTServiceGenerator {

    private static final String API_BASE_URL = "https://api.tinkoff.ru/v1/";
    private static final int CONNECT_TIMEOUT_SECONDS = 15;
    private static final int READ_TIMEOUT_SECONDS = 15;

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
}
