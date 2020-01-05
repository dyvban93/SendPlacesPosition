package com.example.sendplacesposition.retrofit;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String OSM_Search_PLACES_URL = "https://nominatim.openstreetmap.org/";
    private static final String SEND_PLACES_SERVER_URL = "https://app.taxiride.biz:81/api/";
    private static final String OSM_MATRIX_URL = "https://graphhopper.com/api/1/";
    private static final String api_key = "d289c710-3054-40aa-8ee5-851d605f1fa5";

    public static Retrofit getRetrofitOSMInstance(){

            retrofit = new Retrofit.Builder()
                                   .baseUrl(OSM_Search_PLACES_URL)
                                   .addConverterFactory(GsonConverterFactory.create())
                                   .build();

        return retrofit;
    }

    public static Retrofit getRetrofitMatrixInstance(final String body){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        //ajout de l'api_key dans le query parameter
                        HttpUrl url = originalHttpUrl.newBuilder()
                                .addQueryParameter("Key",api_key)
                                .build();
                        Request.Builder requestBuilder = original.newBuilder().url(url);

                        MediaType mediaType = MediaType.parse("application/json");

                        //on passe les éléments du body
                        RequestBody requestBody = RequestBody.create(mediaType,body);

                        Request request = requestBuilder
                                .post(requestBody)
                                .build();

                        return chain.proceed(request);

                    }
                })
                .build();
        retrofit = new Retrofit.Builder()
                                .baseUrl(OSM_MATRIX_URL)
                             //   .client(okHttpClient)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

        

        return retrofit;
    }

    public static Retrofit getRetrofitPlacesInstance(){

        retrofit = new Retrofit.Builder()
                .baseUrl(SEND_PLACES_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

}
