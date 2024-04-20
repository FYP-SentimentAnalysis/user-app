package com.pritamdey.sentimentanalysis;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("user-reviews/{userId}")
    Call<List<Review>> userReviewsList(@Path("userId") String userId);

    @POST("save-review")
    Call<Review> newReview(@Body Review review);

    @POST("user-login")
    Call<User> userLogin(@Body User user);

    @POST("user-signup")
    Call<User> userSignup(@Body User user);

}
