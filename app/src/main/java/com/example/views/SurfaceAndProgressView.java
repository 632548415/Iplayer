package com.example.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.R;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   ISurfaceView
 *  @创建者:   Administrator
 *  @创建时间:  2021/1/17 0017 0:01
 *  @描述：    显示视频
 */
public class SurfaceAndProgressView
        extends RelativeLayout {

    private SurfaceView mSurfaceView;
    private ProgressControlView mProgressControlView;

    public SurfaceAndProgressView(Context context) {
        this(context,null);
    }

    public SurfaceAndProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.my_surfaceview,this);
        mSurfaceView = findViewById(R.id.surface_view);
        mProgressControlView = findViewById(R.id.progress_control);
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    public ProgressControlView getProgressControlView() {
        return mProgressControlView;
    }
}
