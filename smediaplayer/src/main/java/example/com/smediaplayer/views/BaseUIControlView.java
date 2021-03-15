package example.com.smediaplayer.views;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   BaseUIControlView
 *  @创建者:   Administrator
 *  @创建时间:  2021/3/11 0011 21:16
 *  @描述：    TODO
 */
public interface BaseUIControlView {
    /**
     * 更新视频状态
     *
     * 播放错误        -1
     * 等待开始播放     0
     * 准备中          1
     * 准备完成        2
     * 开始播放        3
     * 播放中          4
     * 暂停中          5
     * 缓冲中          6
     * 播放完成        7
     * @param state 状态
     */
    void updatePlayState(int state);

    /**
     * 更新窗口模式
     * WINDOW_MODEL_DEFAULT         默认
     * WINDOW_MODEL_FULLSCREEN      全屏
     * WINDOW_MODEL_SMALL_WINDOW    小窗口
     * @param currentWindowModel
     */
    void updateWindowModel(int currentWindowModel);
}
