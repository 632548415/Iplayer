package com.example.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.R;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   ProgressControlView
 *  @创建者:   Administrator
 *  @创建时间:  2021/1/16 0016 23:52
 *  @描述：    播放进度,暂停/开始,全屏,时间,显示控制view
 */
public class ProgressControlView
        extends RelativeLayout
{

    private ProgressBar mProgressBar;
    private TextView    mCurrentVideoTime;
    private TextView    mMaxVideoTime;

    public ProgressControlView(Context context) {
        this(context, null);
    }

    public ProgressControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_progress_vontrol, this);
        mProgressBar = findViewById(R.id.progress);
        mCurrentVideoTime = findViewById(R.id.current_video_time);
        mMaxVideoTime = findViewById(R.id.max_video_time);
    }

    public void setMaxProgress(int maxProgress) {
        if (mProgressBar != null) { mProgressBar.setMax(maxProgress); }
    }

    public void setProgress(int progress) {
        if (mProgressBar != null) { mProgressBar.setProgress(progress); }
    }

    public void setCurrentTime(String time) {
        if (mCurrentVideoTime != null) {
            mCurrentVideoTime.setText(time);
        }
    }

    public void setMaxTime(String maxTime) {
        if (mMaxVideoTime != null) {
            mMaxVideoTime.setText(maxTime);
        }
    }
}

