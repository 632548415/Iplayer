package example.com.smediaplayer.views;

public interface Play {
    int STATE_ERROR     = -1;        //播放错误
    int STATE_IDLE      = 0;         //等待开始播放
    int STATE_READYING  = 1;         //准备中
    int STATE_READYED   = 2;         //准备完成
    int STATE_START     = 3;         //开始播放
    int STATE_PLAYING   = 4;         //播放中
    int STATE_PAUSED    = 5;         //暂停中
    int STATE_BUFFERING = 6;         //缓冲中
    int STATE_COMPLETE  = 7;         //播放完成

    int WINDOW_MODEL_DEFAULT      = 0; //默认
    int WINDOW_MODEL_FULLSCREEN   = 1; //全屏
    int WINDOW_MODEL_SMALL_WINDOW = 2; //小窗口



    //播放
    void start();

    //暂停
    void pause();

    //是否在播放
    boolean isPlaying();

    //获取视频时长MS
    long getDuration();

    //获取当前播放时长
    long getCurrentPosition();

    //获取缓冲进度
    int getSecondaryProgress();

    //跳转到指定位置
    void seekTo(int progress);

    //获取视频播放窗口的模式
    int getVideoWindowModel();

    //进入全屏
    void inFullScreen();

    //退出全屏
    void exitFullScreen();

    //进去小窗口
    void inSmallWindow();

    //退出小窗口
    void exitSmallWindow();

    //获取播放状态
    int getPlayState();

    //重置
    void reset();

    //重播
    void oneLoop();

    //释放播放器
    void release();

}
