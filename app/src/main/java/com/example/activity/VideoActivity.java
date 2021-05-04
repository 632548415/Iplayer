package com.example.activity;

import android.os.Bundle;

import com.example.R;

import androidx.appcompat.app.AppCompatActivity;
import example.com.smediaplayer.views.SUIControlView;
import example.com.smediaplayer.views.SVideoView;

public class VideoActivity
        extends AppCompatActivity
{
    private SVideoView mSVideoView;
    private String     mUrl = "http://148.70.46.9/456.mp4";
    private String     imageUrl = "https://pic.cnblogs.com/avatar/1142647/20170416093225.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mSVideoView = findViewById(R.id.s_video_view);
        SUIControlView controlView = new SUIControlView(this);
        mSVideoView.addUIContainerView(controlView);
        mSVideoView.setPath(mUrl,null);
        //mSVideoView.setRawSource(R.raw.text);
        //mSVideoView.setAssetsSource("text.mp4");
        mSVideoView.setPreView(imageUrl);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSVideoView != null)
        mSVideoView.release();
    }
}
