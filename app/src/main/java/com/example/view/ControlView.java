package com.example.view;

public interface ControlView {
    //是否在播放
    boolean isPlaying();
    //播放
    void start();

    //暂停
    void pause();

    //更新video状态
    void upDataVideoCurrentState(int state);

    //快进或快退
    void fastBackAndForwarld(int progress);
}
