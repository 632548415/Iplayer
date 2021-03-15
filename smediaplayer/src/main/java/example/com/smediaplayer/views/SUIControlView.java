package example.com.smediaplayer.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import example.com.smediaplayer.R;
import example.com.smediaplayer.utils.SpUtils;
import example.com.smediaplayer.utils.StringUtils;
import example.com.smediaplayer.utils.SystemSettingUtils;


/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   SUIControlView
 *  @创建者:   Administrator
 *  @创建时间:  2021/3/11 0011 21:08
 *  @描述：    UI控制video
 */
public class SUIControlView
        extends RelativeLayout
        implements BaseUIControlView,
                   View.OnClickListener,
                   SeekBar.OnSeekBarChangeListener,
                   View.OnTouchListener
{
    private static final int                MSG_FLAG_PROGRESS           = 1;
    private static final int                MSG_FLAG_SECONDARY_PROGRESS = 2;
    private static final int                MSG_FLAG_TOP_BOTTOM         = 3;
    private static final int                MSG_FLAG_BRIGHTENS_SOUND    = 4;
    private              Context            mContext;
    private              Play               mPlay;
    private              ProgressBar        mProgressBar;
    private              ImageView          mStartAndStop;
    private              TextView           mBackForward;
    private              SoundProgressView  mSoundProgressView;
    private              LinearLayout       mBottomContainer;
    private              LinearLayout       mTopContainer;
    private              TextView           mCurrentTime;
    private              TextView           mMaxTime;
    private              SeekBar            mBottomProgress;
    private              long               mDuration;
    private              int                mUserTouchSeekBarProgress;
    private              boolean            mIsUpdateSeekBar            = true;
    private              boolean            mNeedChangePosition;
    private              boolean            mNeedChangeVolume;
    private              boolean            mNeedChangeBrightness;
    private              float              mDownX;
    private              float              mDownY;
    private              Handler            mHandler                    = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_FLAG_PROGRESS) {
                //更新时间和进度
                long currentPosition = mPlay.getCurrentPosition();
                //保存播放的进度
                SpUtils.savePosition(mContext, currentPosition);
                mCurrentTime.setText(StringUtils.lengthForTime(currentPosition));
                if (mIsUpdateSeekBar) {
                    mBottomProgress.setProgress((int) currentPosition);
                }
            } else if (msg.what == MSG_FLAG_SECONDARY_PROGRESS) {
                //更新缓冲,当secondaryProgress=100的时候缓冲更新完成
                int secondaryProgress = mPlay.getSecondaryProgress();
                if (secondaryProgress == 100) {
                    mHandler.removeCallbacks(mSecondaryProgressRunnable);
                }
                mBottomProgress.setSecondaryProgress((int) (mDuration / 100 * secondaryProgress));
            } else if (msg.what == MSG_FLAG_TOP_BOTTOM) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    mBottomContainer.setVisibility(View.GONE);
                    mBottomContainer.setAnimation(AnimationUtils.loadAnimation(mContext,
                                                                               R.anim.video_bottom_out));
                }
                if (mTopContainer.getVisibility() == View.VISIBLE) {
                    mTopContainer.setVisibility(View.GONE);
                    mTopContainer.setAnimation(AnimationUtils.loadAnimation(mContext,
                                                                            R.anim.video_top_out));
                }
                stopHideTopAndBottomUI();
            } else if (msg.what == MSG_FLAG_BRIGHTENS_SOUND) {
                if (mSoundProgressView.getVisibility() == View.VISIBLE) {
                    mSoundProgressView.setVisibility(View.GONE);
                    mHandler.removeCallbacks(mSoundRunanble);
                }
            }
        }
    };
    private              ImageView          mFullScreent;
    private              ImageView          mBack;
    private              SystemSettingUtils mSettingUtils;
    private              float              mDownBrightnes;
    private              int                mDownCurretVolume;
    private              long               mCurrentPosition;
    private              int                mNewPosition;
    private              long               mHistoryEventTime;
    private              boolean            mIsFirstEventTime           = true;
    private              LinearLayout       mFaustBackContainer;
    private              TextView           mFaustBackType;
    private LinearLayout mErrorContaoner;
    private LinearLayout mCompleteContainer;

    public SUIControlView(Context context) {
        this(context, null);
    }

    public SUIControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_progress_vontrol, this);
        mStartAndStop = findViewById(R.id.start_and_stop);
        mProgressBar = findViewById(R.id.progress_view);
        mBackForward = findViewById(R.id.back_forward);
        mFaustBackContainer = findViewById(R.id.faust_back_Container);
        mFaustBackType = findViewById(R.id.type);


        mSoundProgressView = findViewById(R.id.sound_control_view);
        mBottomContainer = findViewById(R.id.progress_and_time_container);
        mTopContainer = findViewById(R.id.top_ui_container);
        mBack = findViewById(R.id.back);
        mCurrentTime = findViewById(R.id.current_video_time);
        mBottomProgress = findViewById(R.id.bottom_progress);
        mMaxTime = findViewById(R.id.max_video_time);
        mFullScreent = findViewById(R.id.full_screen_iv);
        mErrorContaoner = findViewById(R.id.error_container);
        Button       errorBtn          = findViewById(R.id.error_btn);
        mCompleteContainer = findViewById(R.id.complete_container);
        ImageView resetIv = findViewById(R.id.reset_iv);




        mStartAndStop.setOnClickListener(this);
        mBottomProgress.setOnSeekBarChangeListener(this);
        mFullScreent.setOnClickListener(this);
        mBack.setOnClickListener(this);
        setOnTouchListener(this);
        errorBtn.setOnClickListener(this);
        resetIv.setOnClickListener(this);
        mSettingUtils = new SystemSettingUtils(mContext);

    }

    public void setPlay(Play play) {
        mPlay = play;
    }

    @Override
    public void updatePlayState(int state) {
        mProgressBar.setVisibility(View.GONE);
        mFaustBackContainer.setVisibility(View.GONE);
        mSoundProgressView.setVisibility(View.GONE);
        mErrorContaoner.setVisibility(View.GONE);
        mCompleteContainer.setVisibility(View.GONE);
        switch (state) {
            case Play.STATE_ERROR:          //播放出错
                mErrorContaoner.setVisibility(View.VISIBLE);
                mHandler.removeCallbacks(mRunnable);
                break;
            case Play.STATE_IDLE:           //IDLE状态
            case Play.STATE_READYING:       //准备中
            case Play.STATE_BUFFERING:      //缓冲中
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case Play.STATE_READYED:        //准备完成
                mDuration = mPlay.getDuration();
                mBottomProgress.setMax((int) mDuration);
                mMaxTime.setText(StringUtils.lengthForTime(mDuration));
                break;
            case Play.STATE_PAUSED:         //暂停中
                mStartAndStop.setImageResource(R.mipmap.start);
                mHandler.removeCallbacks(mRunnable);
                break;
            case Play.STATE_START:          //开始播放
            case Play.STATE_PLAYING:        //播放中
                mStartAndStop.setImageResource(R.mipmap.stop);
                //更新播放时间和进度
                mHandler.postDelayed(mRunnable, 1000);
                //更新缓冲进度
                mHandler.postDelayed(mSecondaryProgressRunnable, 1000);
                startHideTopAndBottomUI();
                break;
            case Play.STATE_COMPLETE:       //播放完成
                mCompleteContainer.setVisibility(View.VISIBLE);
                mHandler.removeCallbacks(mRunnable);
                break;
            default:
                break;
        }
    }


    @Override
    public void updateWindowModel(int currentWindowModel) {
        switch (currentWindowModel) {
            case Play.WINDOW_MODEL_DEFAULT:                     //默认
                mFullScreent.setImageResource(R.mipmap.full_screen);
                mTopContainer.setVisibility(View.GONE);
                break;
            case Play.WINDOW_MODEL_FULLSCREEN:                  //全屏
                mFullScreent.setImageResource(R.mipmap.out_full_screen);
                mTopContainer.setVisibility(View.VISIBLE);
                break;
            case Play.WINDOW_MODEL_SMALL_WINDOW:                //小窗口
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.full_screen_iv) {
            int videoWindowModel = mPlay.getVideoWindowModel();
            if (videoWindowModel == Play.WINDOW_MODEL_DEFAULT) {
                mPlay.inFullScreen();
            } else if (videoWindowModel == Play.WINDOW_MODEL_FULLSCREEN) {
                mPlay.exitFullScreen();
            }
        } else if (id == R.id.start_and_stop) {
            if (mPlay.isPlaying()) {
                mPlay.pause();
            } else {
                mPlay.start();
            }
        } else if (id == R.id.back) {
            //退出全屏
            mPlay.exitFullScreen();
        }else if (id == R.id.error_btn){
            //重试播放
            mPlay.reset();
        }else if (id == R.id.reset_iv){
            //重播
            mPlay.oneLoop();
        }

    }

    //开始top,bottom的显示和隐藏的任务
    private void startHideTopAndBottomUI() {
        mHandler.postDelayed(mTopAndBottomUIRun, 3000);
    }

    //停止top,bottom的显示和隐藏的任务
    private void stopHideTopAndBottomUI() {
        mHandler.removeCallbacks(mTopAndBottomUIRun);
    }

    //显示声音和亮度的UI
    private void showSoundUI(boolean isSound) {
        mSoundProgressView.setVisibility(View.VISIBLE);
        if (isSound) {
            mSoundProgressView.setIcon(R.mipmap.sound);
        } else {
            mSoundProgressView.setIcon(R.mipmap.brigh);
        }
    }

    //隐藏声音和亮度的UI
    private void hideSoundUI() {
        mHandler.postDelayed(mSoundRunanble, 1000);
    }

    private Runnable mTopAndBottomUIRun = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_FLAG_TOP_BOTTOM);
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_FLAG_PROGRESS);
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private Runnable mSecondaryProgressRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_FLAG_SECONDARY_PROGRESS);
            mHandler.postDelayed(mSecondaryProgressRunnable, 1000);
        }
    };
    private Runnable mSoundRunanble             = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_FLAG_BRIGHTENS_SOUND);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mUserTouchSeekBarProgress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //当用户开始触摸时停止更新SeekBar
        mIsUpdateSeekBar = false;
        stopHideTopAndBottomUI();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mIsUpdateSeekBar = true;
        mPlay.seekTo(mUserTouchSeekBarProgress);
        startHideTopAndBottomUI();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int playState = mPlay.getPlayState();
        int playModel = mPlay.getVideoWindowModel();
        if (playState == Play.STATE_ERROR || playState == Play.STATE_IDLE || playState == Play.STATE_READYING || playState == Play.STATE_READYED || playState == Play.STATE_COMPLETE || playModel == Play.WINDOW_MODEL_SMALL_WINDOW) {
            //当不是播放中暂停中缓冲中的时候不做处理
            return false;
        }
        int   action = event.getAction();
        float x      = event.getX();
        float y      = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangeBrightness = false;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float tempX = x - mDownX;
                float tempY = y - mDownY;
                float descX = Math.abs(x - mDownX);
                float descY = Math.abs(y - mDownY);
                if (!mNeedChangeVolume && !mNeedChangePosition && !mNeedChangeBrightness) {
                    if (descX >= 10) {
                        //修改进度
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.removeCallbacks(mSecondaryProgressRunnable);
                        mCurrentPosition = mPlay.getCurrentPosition();
                        mNeedChangePosition = true;
                    } else if (descY >= 10) {
                        if (mDownX < getWidth() * .5f) {
                            //修改亮度
                            mNeedChangeBrightness = true;
                            mDownBrightnes = mSettingUtils.getBrightnes();
                        } else {
                            //修改音量
                            mNeedChangeVolume = true;
                            mDownCurretVolume = mSettingUtils.getCurretVolume();
                        }
                    }
                }
                if (mNeedChangeVolume) {
                    showSoundUI(true);
                    tempY = -tempY;
                    int maxVolume   = mSettingUtils.getMaxVoluem();
                    int deltaVolume = (int) (maxVolume * tempY * 3 / getHeight());
                    int newVolume   = mDownCurretVolume + deltaVolume;
                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                    mSettingUtils.setVoluem(newVolume);
                    mSoundProgressView.setVolume(maxVolume, newVolume);
                }
                if (mNeedChangeBrightness) {
                    showSoundUI(false);
                    tempY = -tempY;
                    float deltaBrightness     = tempY * 3 / getHeight();
                    float newresultBrightness = mDownBrightnes + deltaBrightness;
                    newresultBrightness = Math.max(0, Math.min(newresultBrightness, 1));
                    mSettingUtils.setBrightnes(newresultBrightness);
                    mSoundProgressView.setBrightnes(newresultBrightness);
                }
                if (mNeedChangePosition) {
                    long duration   = mPlay.getDuration();
                    long toPosition = (long) (mCurrentPosition + duration * tempX / getWidth());
                    mNewPosition = (int) Math.max(0, Math.min(duration, toPosition));
                    int  newPositionProgress = (int) (100f * mNewPosition / duration);
                    long ms                  = (long) (duration * newPositionProgress / 100f);
                    mFaustBackContainer.setVisibility(View.VISIBLE);
                    mBackForward.setText(StringUtils.lengthForTime(ms) + " / " + StringUtils.lengthForTime(duration));
                    long currentPosition = mPlay.getCurrentPosition();
                    if (mNewPosition > currentPosition) {
                        mFaustBackType.setText(R.string.faust);
                    } else {
                        mFaustBackType.setText(R.string.back);
                    }
                    mBottomProgress.setProgress(mNewPosition);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mNeedChangeVolume && !mNeedChangePosition && !mNeedChangeBrightness) {
                    if (mIsFirstEventTime) {
                        mIsFirstEventTime = false;
                        mHistoryEventTime = event.getEventTime();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIsFirstEventTime = true;
                            }
                        }, 1000);
                    } else {
                        long eventTime = event.getEventTime();
                        if (Math.abs(mHistoryEventTime - eventTime) <= 800) {
                            mIsFirstEventTime = true;
                            //触发双击事件
                            if (mPlay.isPlaying()) {
                                mPlay.pause();
                            } else {
                                mPlay.start();
                            }
                            return true;
                        } else {
                            mIsFirstEventTime = true;
                        }
                    }
                }

                if (mNeedChangeVolume) {
                    mNeedChangeVolume = false;
                    hideSoundUI();
                    return true;
                }
                if (mNeedChangeBrightness) {
                    mNeedChangeBrightness = false;
                    hideSoundUI();
                    return true;
                }
                if (mNeedChangePosition) {
                    mNeedChangePosition = false;
                    mPlay.seekTo(mNewPosition);
                    mFaustBackContainer.setVisibility(View.GONE);
                    //更新播放时间和进度
                    mHandler.postDelayed(mRunnable, 1000);
                    //更新缓冲进度
                    mHandler.postDelayed(mSecondaryProgressRunnable, 1000);
                    return true;
                }
                mBottomContainer.setVisibility(View.VISIBLE);
                mBottomContainer.setAnimation(AnimationUtils.loadAnimation(mContext,
                                                                           R.anim.video_bottom_in));
                if (mPlay.getVideoWindowModel() == Play.WINDOW_MODEL_FULLSCREEN) {
                    mTopContainer.setVisibility(View.VISIBLE);
                    mTopContainer.setAnimation(AnimationUtils.loadAnimation(mContext,
                                                                            R.anim.video_top_in));
                }
                startHideTopAndBottomUI();
                break;
        }
        return true;
    }

    public void release() {
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
