package com.example.activity;

import android.os.Bundle;

import com.example.R;

import androidx.appcompat.app.AppCompatActivity;
import example.com.smediaplayer.views.SUIControlView;
import example.com.smediaplayer.views.SVideoView;

public class MainActivity extends AppCompatActivity {

    private SVideoView mSVideoView;
    private String mUrl = "http://148.70.46.9/456.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSVideoView = findViewById(R.id.s_video_view);
        SUIControlView controlView = new SUIControlView(this);
        mSVideoView.addUIContainerView(controlView);
        //mSVideoView.setUp(mUrl,null);
        //mSVideoView.setRawSource(R.raw.text);
        mSVideoView.setAssetsSource("text.mp4");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSVideoView.release();
    }
}
