package com.example.gaetan.babyphoneapp.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    @FormUrlEncoded
    @POST("/signin")
    Call<String> signinUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/signup")
    Call<String> signupUser(@Field("email") String email, @Field("password") String password, @Field("serial") String babyphoneId);
}
