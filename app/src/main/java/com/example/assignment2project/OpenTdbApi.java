package com.example.assignment2project;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenTdbApi {

    @GET("api.php")
    Call<OpenTdbResponse> getQuestions(
            @Query("amount") int amount,
            @Query("category") Integer category,
            @Query("difficulty") String difficulty,
            @Query("type") String type
    );
}
