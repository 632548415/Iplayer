package example.com.smediaplayer.views;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import example.com.smediaplayer.utils.SpUtils;
import example.com.smediaplayer.utils.StringUtils;

import static android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END;
import static android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START;
import static android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   SVideoView
 *  @创建者:   Administrator
 *  @创建时间:  2021/3/10 0010 23:50
 *  @描述：    MediaPlayer控制
 */
public class SVideoView
        extends FrameLayout
        implements Play,
                   TextureView.SurfaceTextureListener,
                   MediaPlayer.OnPreparedListener,
                   MediaPlayer.OnBufferingUpdateListener,
                   MediaPlayer.OnCompletionListener,
                   MediaPlayer.OnErrorListener,
                   MediaPlayer.OnSeekCompleteListener,
                   MediaPlayer.OnInfoListener
{

    private int                 mCurrentWindowModel = WINDOW_MODEL_DEFAULT;              //当前窗口模式
    private int                 mCurrentPalyState   = STATE_IDLE;                        //当前播放状态 http://gslb.miaopai.com/stream/oxX3t3Vm5XPHKUeTS-zbXA__.mp4
    private String              mPath;                      //http://148.70.46.9/456.mp4
    private Map<String, String> mHeaders;
    private Context             mContext;
    private CustomTextureView   mTextureView;
    private MediaPlayer         mMediaPlayer;
    private SurfaceTexture      mSurfaceTexture;
    private Surface             mSurface;
    private FrameLayout         mContainer;
    private SUIControlView      mSUIControlView;
    private int                 mSecondaryProgress;      //缓冲进度百分比 最大100
    private boolean             mIsReset;//播放是否重新开始过
    private AssetFileDescriptor mAfd;
    private AssetManager mAssets;

    public SVideoView(@NonNull Context context) {
        this(context, null);
    }

    public SVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mContainer, lp);

        initTextureView();
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new CustomTextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
        addTextureView();
    }

    private void addTextureView() {
        if (mContainer != null) {
            mContainer.removeView(mTextureView);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                                               LayoutParams.MATCH_PARENT,
                                               Gravity.CENTER);
            mContainer.addView(mTextureView, 0, lp);
        }
    }

    public void addUIContainerView(SUIControlView controlView) {
        mContainer.removeView(mSUIControlView);
        mSUIControlView = controlView;
        mSUIControlView.setPlay(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContainer.addView(mSUIControlView, lp);
    }

    private void initVideoPLay() {
        if (mMediaPlayer == null) {
            if (mSurface == null) {
                mSurface = new Surface(mSurfaceTexture);
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnInfoListener(this);
            try {
                if (mAfd != null) {
                    mMediaPlayer.setDataSource(mAfd.getFileDescriptor(),
                                               mAfd.getStartOffset(),
                                               mAfd.getLength());
                } else {
                    mMediaPlayer.setDataSource(mContext, Uri.parse(mPath), mHeaders);
                }
                mCurrentPalyState = STATE_READYING;
                mSUIControlView.updatePlayState(mCurrentPalyState);
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                Log.d("TAG", "initVideoPLay: IO错误 或 mPath=null");
            }
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //SurfaceTexture准备好使用时调用。全屏的时候surface会被释放掉重新创建新的
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            initVideoPLay();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //SurfaceTexture的缓冲区大小更改时调用
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //SurfaceTexture即将被销毁时调用
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //更新SurfaceTexture调用
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //视频异步准备完成调用
        if (mIsReset) {
            //跳转到指定位置重新播放
            int position = SpUtils.getPosition(mContext);
            mp.seekTo(position);
            mp.start();
            mCurrentPalyState = STATE_PLAYING;
            mSUIControlView.updatePlayState(mCurrentPalyState);
            mIsReset = false;
            SpUtils.savePosition(mContext, 0);
        } else {
            mCurrentPalyState = STATE_READYED;
            mSUIControlView.updatePlayState(mCurrentPalyState);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //视频缓存更新调用
        mSecondaryProgress = percent;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //视频播放完成时调用
        mCurrentPalyState = STATE_COMPLETE;
        mSUIControlView.updatePlayState(mCurrentPalyState);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放发生错误的时候调用,true自己处理
        //what   MEDIA_ERROR_SERVER_DIED服务器错误
        //extra  MEDIA_ERROR_IO文件或网络相关错误
        //extra  MEDIA_ERROR_MALFORMED不是视频文件
        //extra  MEDIA_ERROR_UNSUPPORTED播放器不支持该格式的文件
        mCurrentPalyState = STATE_ERROR;
        mSUIControlView.updatePlayState(mCurrentPalyState);
        return true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //在seek操作完成时调用的回调
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //处理一些视频的信息
        //MEDIA_INFO_VIDEO_RENDERING_START开始播放第一帧数据
        //MEDIA_INFO_BUFFERING_START (701) 暂停播放,缓冲更多的数据
        //MEDIA_INFO_BUFFERING_END (702) 缓冲结束,回复播放了
        //MEDIA_INFO_NETWORK_BANDWIDTH (703)
        if (what == MEDIA_INFO_VIDEO_RENDERING_START) {
            mCurrentPalyState = STATE_PLAYING;
        } else if (what == MEDIA_INFO_BUFFERING_START) {
            mCurrentPalyState = STATE_BUFFERING;
        } else if (what == MEDIA_INFO_BUFFERING_END) {
            mCurrentPalyState = STATE_PLAYING;
        }
        mSUIControlView.updatePlayState(mCurrentPalyState);
        return true;
    }

    @Override
    public void start() {
        if (mCurrentPalyState == STATE_READYED || mCurrentPalyState == STATE_PAUSED) {
            mCurrentPalyState = STATE_PLAYING;
            mSUIControlView.updatePlayState(mCurrentPalyState);
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mCurrentPalyState == STATE_PLAYING && mMediaPlayer != null) {
            mCurrentPalyState = STATE_PAUSED;
            mSUIControlView.updatePlayState(mCurrentPalyState);
            mMediaPlayer.pause();
        }
    }


    @Override
    public boolean isPlaying() {
        if (mMediaPlayer == null) {
            return false;
        }
        return mMediaPlayer.isPlaying();
    }


    @Override
    public void seekTo(int progress) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getSecondaryProgress() {
        return mSecondaryProgress;
    }

    @Override
    public int getVideoWindowModel() {
        return mCurrentWindowModel;
    }

    @Override
    public void inFullScreen() {
        if (mCurrentWindowModel == WINDOW_MODEL_DEFAULT) {
            //隐藏actionBar
            StringUtils.hideActionBar(mContext);
            //横屏显示
            StringUtils.transActivity(mContext)
                       .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //获取跟布局
            ViewGroup contentView = StringUtils.transActivity(mContext)
                                               .findViewById(android.R.id.content);
            removeView(mContainer);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                   ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.addView(mContainer, params);
            mCurrentWindowModel = WINDOW_MODEL_FULLSCREEN;
            mSUIControlView.updateWindowModel(mCurrentWindowModel);
            if (mMediaPlayer != null) {
                int videoWidth  = mMediaPlayer.getVideoWidth();
                int videoHeight = mMediaPlayer.getVideoHeight();
                mTextureView.inFullScreen(videoWidth, videoHeight);
            }
        }
    }

    @Override
    public void exitFullScreen() {
        if (mCurrentWindowModel == WINDOW_MODEL_FULLSCREEN) {
            StringUtils.showActionBar(mContext);
            //竖屏显示
            StringUtils.transActivity(mContext)
                       .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ViewGroup contentView = StringUtils.transActivity(mContext)
                                               .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            addView(mContainer);
            mCurrentWindowModel = WINDOW_MODEL_DEFAULT;
            mSUIControlView.updateWindowModel(mCurrentWindowModel);
            mTextureView.inFullScreen(0, 0);
        }
    }

    @Override
    public void inSmallWindow() {

    }

    @Override
    public void exitSmallWindow() {

    }

    @Override
    public int getPlayState() {
        return mCurrentPalyState;
    }

    @Override
    public void oneLoop() {
        if (mMediaPlayer != null && mCurrentPalyState == STATE_COMPLETE && !isPlaying()) {
            mMediaPlayer.seekTo(0);
            mMediaPlayer.start();
            mCurrentPalyState = STATE_PLAYING;
            mSUIControlView.updatePlayState(mCurrentPalyState);
        }
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mIsReset = true;
            mMediaPlayer.reset();
            try {
                if (mAfd != null) {
                    mMediaPlayer.setDataSource(mAfd.getFileDescriptor(),
                                               mAfd.getStartOffset(),
                                               mAfd.getLength());
                } else {
                    mMediaPlayer.setDataSource(mContext, Uri.parse(mPath), mHeaders);
                }

                mMediaPlayer.prepareAsync();
                mCurrentPalyState = STATE_READYING;
                mSUIControlView.updatePlayState(mCurrentPalyState);
            } catch (Exception e) {
                Log.d("TAG", "initVideoPLay: IO错误 或 mPath=null");
            }

        }
    }

    //设置网络/文件视频
    public void setUp(String path, Map<String, String> headers) {
        mPath = path;
        mHeaders = headers;
    }

    //设置raw资源
    public void setRawSource(int id) {
        if (id <= 0) {
            return;
        }
        mAfd = getResources().openRawResourceFd(id);
    }

    //设置Assets资源
    public void setAssetsSource(String sourceName) {
        if (sourceName == null) {
            return;
        }
        mAssets = mContext.getAssets();
        try {
            mAfd = mAssets.openFd(sourceName);
        } catch (IOException e) {
            Log.d("TAG", "initVideoPLay: assets资源错误");
        }

    }

    @Override
    public void release() {
        if (mSUIControlView != null) {
            mSUIControlView.release();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mTextureView != null) {
            mTextureView = null;
        }
        if (mAfd != null) {
            try {
                mAfd.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mAfd != null) {
                    try {
                        mAfd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (mAssets != null){
            mAssets.close();
        }
        mCurrentPalyState = STATE_IDLE;
    }
}
