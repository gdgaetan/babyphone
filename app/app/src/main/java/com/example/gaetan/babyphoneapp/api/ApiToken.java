package com.example.gaetan.babyphoneapp.api;

public class ApiToken {
    private static String token;
    private static String videoUrl;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        ApiToken.token = token;
    }

    public static String getVideoUrl() {
        return videoUrl;
    }

    public static void setVideoUrl(String videoUrl) {
        ApiToken.videoUrl = videoUrl+"?action=stream";
    }
}
