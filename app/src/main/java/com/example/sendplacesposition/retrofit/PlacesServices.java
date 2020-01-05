package com.example.sendplacesposition.retrofit;

import com.example.sendplacesposition.osmmatrix.MatrixResponse;
import com.example.sendplacesposition.osmplaces.OSMPlaces;
import com.example.sendplacesposition.osmplaces.cityPlaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface PlacesServices {

    @POST("api/places/postNewPlace")
    Call<cityPlaces> postNewPlace(@Body cityPlaces places);

    @Headers({
            "User-Agent: Retrofit-Sample-App"
    })
    @GET
    Call<List<OSMPlaces>> getOSMPlacesDetails(@Url String url);

    @Headers({
            "User-Agent: Retrofit-Sample-App"
    })
    @GET
    Call<MatrixResponse> getMatrixInformations(@Url String url);
}
