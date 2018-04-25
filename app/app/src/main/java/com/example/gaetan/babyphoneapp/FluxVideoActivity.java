package com.example.gaetan.babyphoneapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.gaetan.babyphoneapp.api.ApiToken;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.*;


import butterknife.BindView;
import butterknife.ButterKnife;


public class FluxVideoActivity extends AppCompatActivity {
    private static final int TIMEOUT = 5;

    @BindView(R.id.video)
    MjpegView mjpegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flux_video);
        ButterKnife.bind(this);



        Mjpeg.newInstance()
                .open(ApiToken.getVideoUrl(), TIMEOUT)
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                });

    }
    public void deconnection(View view) {
        ApiToken.setToken("");
        ApiToken.setVideoUrl("");
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

}