package com.example;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.utils.StringUtils;
import com.example.view.ControlView;
import com.example.views.ProgressControlView;
import com.example.views.SurfaceAndProgressView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example
 *  @文件名:   MediaPalyerControl
 *  @创建者:   Administrator
 *  @创建时间:  2021/1/16 0016 22:36
 *  @描述：    TODO
 */
public class MediaPalyerControl
        implements MediaPlayer.OnPreparedListener, SurfaceHolder.Callback, ControlView, MediaPlayer.OnBufferingUpdateListener {
    public static final int READING = 0;            //缓冲中
    public static final int PREPARED = 1;           //准备完成
    public static final int PLAYING = 2;            //播放中
    public static final int SUSPEND = 3;            //暂停中
    public static final int CONTUIN_PLAYING = 4;    //继续播放
    public static final int STOP = 5;               //停止
    private int mCurrentPlayState = READING;
    private boolean mIsLoop = false;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private String mUrl = "http://148.70.46.9/456.mp4";
    private SurfaceView mSurfaceView;
    private ProgressControlView mProgressControlView;
    private Timer mTimer;

    public MediaPalyerControl(SurfaceAndProgressView surfaceAndProgressView, Context context) {
        mContext = context;
        mSurfaceView = surfaceAndProgressView.getSurfaceView();
        mProgressControlView = surfaceAndProgressView.getProgressControlView();
        mProgressControlView.setView(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
    }

    /**
     * 视屏的状态，准备中，准备完成，播放中，暂停中，继续播放，播放完成，
     *
     * @param mp
     */

    @Override
    public void onPrepared(MediaPlayer mp) {
        //视频准备完成时触发
        upDataVideoCurrentState(PREPARED);
        mProgressControlView.setMaxProgress(mp.getDuration());
        mProgressControlView.setMaxTime(StringUtils.lengthForTime(mp.getDuration()));
        mMediaPlayer.setDisplay(mSurfaceHolder);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, Uri.parse(mUrl));
            mMediaPlayer.setLooping(mIsLoop);
            mMediaPlayer.prepareAsync();
            //开始准备视屏
            upDataVideoCurrentState(READING);
            mTimer = new Timer();
            mTimer.schedule(mTimerTask, 1000, 1000);
        } catch (IOException e) {
            Log.d(TAG, "MediaPalyerControl: data source error");
        }
    }

    private Handler mProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressControlView.setProgress(mMediaPlayer.getCurrentPosition());
            String time = StringUtils.lengthForTime(mMediaPlayer.getCurrentPosition());
            mProgressControlView.setCurrentTime(time);
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mProgressHandler.sendEmptyMessage(0);
            }
        }
    };

    /**
     * 设置循环播放
     *
     * @param loop true 循环播放
     */
    public void setLoop(boolean loop) {
        mIsLoop = loop;
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if (mMediaPlayer != null && (mCurrentPlayState == PREPARED | mCurrentPlayState == SUSPEND) && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            upDataVideoCurrentState(PLAYING);
        }
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (mMediaPlayer != null && mCurrentPlayState == PLAYING && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            upDataVideoCurrentState(SUSPEND);
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * 跟新视屏当前状态
     *
     * @param state
     */
    @Override
    public void upDataVideoCurrentState(int state) {
        mCurrentPlayState = state;
        mProgressControlView.upDataVideoState(state);
    }

    @Override
    public void fastBackAndForwarld(int progress) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
            upDataVideoCurrentState(READING);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓冲或播放的百分比percent
        if (mCurrentPlayState == READING) {
            upDataVideoCurrentState(PLAYING);
        }
    }
}
