package com.example.gaetan.babyphoneapp.api;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UserApi {
    private Retrofit retrofit;

    final static String URL = "http://54.37.11.19";

    public UserApi() {
         retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public void signin(String email, String password, OnSuccess success, OnFailure failure) {
        UserService service = retrofit.create(UserService.class);
        service.signinUser(email, password)
            .enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    JsonParser parser = new JsonParser();
                    if(response.isSuccessful()) {
                        JsonObject o = parser.parse(response.body().toString()).getAsJsonObject();
                        String token = o.get("data").getAsJsonObject().get("token").getAsString();
                        String videoUrl = o.get("data").getAsJsonObject().get("video_url").getAsString();
                        ApiToken.setToken(token);
                        ApiToken.setVideoUrl(videoUrl);
                        success.execute(token);
                    } else {
                        if(response.errorBody() == null ) {
                            failure.execute( response.code() + " error" );
                        }
                        else
                            try {
                                JsonObject o = parser.parse(response.errorBody().string()).getAsJsonObject();
                                failure.execute(o.get("error").getAsString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("error", t.getLocalizedMessage());
                    Log.e("error", t.getMessage());
                }
            });
    }

    public void signup(String email, String password, String babyphoneId, OnSuccess success, OnFailure failure) {
        UserService service = retrofit.create(UserService.class);
        service.signupUser(email, password, babyphoneId)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        JsonParser parser = new JsonParser();
                        if(response.isSuccessful()) {
                            JsonObject o = parser.parse(response.body().toString()).getAsJsonObject();
                            String token = o.get("data").getAsJsonObject().get("token").getAsString();
                            String videoUrl = o.get("data").getAsJsonObject().get("video_url").getAsString();
                            ApiToken.setToken(token);
                            ApiToken.setVideoUrl(videoUrl);
                            success.execute(token);
                        } else {
                            if(response.errorBody()== null ) {
                                failure.execute( response.code() + " error" );
                            }
                            else
                                try {
                                    JsonObject o = parser.parse(response.errorBody().string()).getAsJsonObject();
                                    failure.execute(o.get("error").getAsString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("error", t.getLocalizedMessage());
                        Log.e("error", t.getMessage());
                    }
                });
    }
}
