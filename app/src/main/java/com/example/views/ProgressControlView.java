package com.example.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowId;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.MediaPalyerControl;
import com.example.R;
import com.example.utils.SystemSettingUtils;
import com.example.view.ControlView;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   ProgressControlView
 *  @创建者:   Administrator
 *  @创建时间:  2021/1/16 0016 23:52
 *  @描述：    播放进度,暂停/开始,全屏,时间,显示控制view
 */
public class ProgressControlView
        extends RelativeLayout implements View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "ProgressControlView";
    private SeekBar mProgressBar;
    private TextView mCurrentVideoTime;
    private TextView mMaxVideoTime;
    private ImageView mStartAndStop;
    private int mVideoState;
    private ControlView mControlView;
    private SoundProgressView mSoundProgressView;
    private Context mContext;
    private LinearLayout mProgressAndTime;
    private Handler mHandler;
    private ProgressBar mLoadingProgress;
    private SystemSettingUtils mSettingUtils;
    private int maxVoluem;
    private int curretVolume;
    private float mBrightnes;
    private boolean isUserTouchProgress = false;

    public ProgressControlView(Context context) {
        this(context, null);
    }

    public ProgressControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHandler = new Handler();
        View.inflate(context, R.layout.view_progress_vontrol, this);
        mProgressBar = findViewById(R.id.progress);
        mCurrentVideoTime = findViewById(R.id.current_video_time);
        mMaxVideoTime = findViewById(R.id.max_video_time);
        mStartAndStop = findViewById(R.id.start_and_stop);
        mSoundProgressView = findViewById(R.id.sound_control_view);
        mProgressAndTime = findViewById(R.id.progress_and_time_container);
        mLoadingProgress = findViewById(R.id.progress_bar);
        mStartAndStop.setOnClickListener(this);
        mProgressBar.setOnSeekBarChangeListener(this);
        setOnTouchListener(this);

        mSettingUtils = new SystemSettingUtils(context);
        maxVoluem = mSettingUtils.getMaxVoluem();


    }

    public void setView(ControlView view) {
        mControlView = view;
    }

    public void setMaxProgress(int maxProgress) {
        if (mProgressBar != null) {
            mProgressBar.setMax(maxProgress);
        }
    }

    public void setProgress(int progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
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

    /**
     * 更新视屏播放状态
     *
     * @param videoState 视屏当前的播放状态
     */
    public void upDataVideoState(int videoState) {
        mVideoState = videoState;
        if (videoState == MediaPalyerControl.READING && mLoadingProgress.getVisibility() == View.GONE) {
            //视屏在缓冲中
            mLoadingProgress.setVisibility(View.VISIBLE);
            mStartAndStop.setVisibility(View.GONE);
        } else {
            if (videoState == MediaPalyerControl.PREPARED) {
                //准备完成，显示开始按钮，隐藏加载控件
                mStartAndStop.setVisibility(View.VISIBLE);
                mLoadingProgress.setVisibility(View.GONE);
                mStartAndStop.setImageResource(R.mipmap.start);
            } else if (videoState == MediaPalyerControl.SUSPEND){
                //暂停中
                mStartAndStop.setVisibility(View.VISIBLE);
                mStartAndStop.setImageResource(R.mipmap.start);
            }else if (videoState == MediaPalyerControl.PLAYING){
                //播放中
                mLoadingProgress.setVisibility(View.GONE);
                mStartAndStop.setVisibility(View.GONE);
                mStartAndStop.setImageResource(R.mipmap.stop);
            }else {
                upDataControlUI();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mVideoState == MediaPalyerControl.PLAYING) {
            //视屏在播放中，点击了暂停按钮
            mControlView.pause();
        } else if (mVideoState == MediaPalyerControl.SUSPEND) {
            //暂停中
            mControlView.start();
        } else if (mVideoState == MediaPalyerControl.PREPARED) {
            //准备完成状态
            mControlView.start();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            //当点击发生时被触发
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //用户已经执行了down，但还没有执行move或up
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //当触发它的up{@link MotionEvent}发生点击时通知
            //控制显示暂停，进度条，声音
            upDataControlUI();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //当滚动发生时，初始的向下{@link MotionEvent}和当前的移动{@link MotionEvent}会被通知
            int width = getWidth() / 2;
            float downX = e1.getX();
            float downY = e1.getY();
            float moveY = e2.getY();
            float moveX = e2.getX();
            if (downX > width) {
                //判断是上下还是左右滑动
                float dexY = downY - moveY;
                if (Math.abs(dexY) > 10){
                    Log.d(TAG, "onScroll: 上下滑动"+dexY);
                }else {

                }
                mSoundProgressView.setIcon(R.mipmap.sound);
                mSoundProgressView.setVisibility(View.VISIBLE);
                //控制音量或亮度的时候隐藏开始暂停按钮
                mProgressAndTime.setVisibility(View.GONE);
                mStartAndStop.setVisibility(View.GONE);
                curretVolume = mSettingUtils.getCurretVolume();
                //点击的位置是右边
                float temp = (downY - moveY) / getHeight();
                int index = (int) (temp * maxVoluem) + curretVolume;
                if (index > maxVoluem)
                    index = maxVoluem;
                if (index < 0)
                    index = 0;
                mSettingUtils.setVoluem(index);
                mSoundProgressView.setVolume(maxVoluem,index);
            } else {
                mSoundProgressView.setIcon(R.mipmap.brigh);
                mSoundProgressView.setVisibility(View.VISIBLE);
                mProgressAndTime.setVisibility(View.GONE);
                mStartAndStop.setVisibility(View.GONE);
                //点击的位置是左边
                mBrightnes = (downY - moveY) / getHeight();
                float brightnes = mSettingUtils.getBrightnes()+mBrightnes;
                if (brightnes > 1){
                    brightnes = 1;
                }
                if (brightnes <= 0){
                    brightnes = 0;
                }
                mSettingUtils.setBrightnes(brightnes);
                mSoundProgressView.setBrightnes(brightnes);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //当长时间按下触发它的初始on down{@link MotionEvent}时发出通知
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //on 和 up都完成时触发，沿x轴和y轴提供计算的速度（像素/秒）
            refeshUI();
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //完成双击时通知
            Log.d(TAG, "onDoubleTap: onDoubleTap");
            return super.onDoubleTap(e);
        }
    });

    /**
     * 刷线UI
     */
    private void refeshUI() {

        mHandler.removeCallbacks(mRunnable);
        //倒计时隐藏已经显示的
        mHandler.postDelayed(mControlRunnable, 1000);
    }
    private Runnable mControlRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSoundProgressView.getVisibility() == View.VISIBLE){
                mSoundProgressView.setVisibility(View.GONE);
            }
        }
    };

    /**
     * 更新控制UI，进度和声音和暂停按钮
     */
    private void upDataControlUI() {
        mSoundProgressView.setVisibility(View.GONE);
        mProgressAndTime.setVisibility(View.VISIBLE);
        mStartAndStop.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mRunnable);
        //倒计时隐藏已经显示的
        mHandler.postDelayed(mRunnable, 2000);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mProgressAndTime.getVisibility() == View.VISIBLE && !isUserTouchProgress) {
                mProgressAndTime.setVisibility(View.GONE);
            }
            if (mVideoState == MediaPalyerControl.PLAYING || mVideoState == MediaPalyerControl.CONTUIN_PLAYING) {
                mStartAndStop.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && mControlView.isPlaying()) {
            isUserTouchProgress = true;
            //mControlView.pause();
            mControlView.fastBackAndForwarld(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch: ----------");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isUserTouchProgress = false;
        upDataControlUI();
    }
}

