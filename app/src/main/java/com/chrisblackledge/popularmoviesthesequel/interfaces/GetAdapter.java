package com.chrisblackledge.popularmoviesthesequel.interfaces;

import retrofit2.Call;

import com.chrisblackledge.popularmoviesthesequel.model.MovieParcel_List;
import com.chrisblackledge.popularmoviesthesequel.model.ReviewParcel_List;
import com.chrisblackledge.popularmoviesthesequel.model.VideoParcel_List;

import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.JacksonConverterFactory;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetAdapter {

    @GET("/3/movie/{list_type}")
    Call<MovieParcel_List> getMovieList(@Path("list_type") String listType, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/videos")
    Call<VideoParcel_List> getMovieTrailers(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Call<ReviewParcel_List> getMovieReviews(@Path("id") int id, @Query("api_key") String apiKey);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
}

