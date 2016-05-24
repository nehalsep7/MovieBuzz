package com.example.android.moviebuzzapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by kumnehal on 05/21/16.
 */
public interface ApiInterface {
        @GET("movie/popular")
        Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

        @GET("movie/top_rated")
        Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

        @GET("movie/upcoming")
        Call<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey);

        @GET("movie/now_playing")
        Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

        @GET("movie/{id}")
        Call<MovieResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

        @GET("movie/{id}/videos")
        Call<VideoResponse> getVideoDetails(@Path("id") int id, @Query("api_key") String apiKey);

}
