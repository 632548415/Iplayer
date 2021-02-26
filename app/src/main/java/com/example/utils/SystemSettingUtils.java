package com.example.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import static android.content.ContentValues.TAG;

public class SystemSettingUtils {

    private final Context mApplicationContext;
    private final AudioManager audioManager;
    private final Window mWindow;

    public SystemSettingUtils(Context context) {
        mApplicationContext = context.getApplicationContext();
        audioManager = (AudioManager) mApplicationContext.getSystemService(Context.AUDIO_SERVICE);
        mWindow = ((Activity) context).getWindow();
    }

    /**
     * 获取当前系统音量
     *
     * @return 音量数值
     */
    public int getCurretVolume() {
        if (audioManager != null) {
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return -1;
    }

    /**
     * 获取系统最大音量
     *
     * @return 最大音量
     */
    public int getMaxVoluem() {
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return -1;
    }

    /**
     * 设置音量
     *
     * @param index 音量
     */
    public void setVoluem(int index) {
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        }
    }

    /**
     * 获取亮度
     *
     * @return 亮度值
     */
    public float getBrightnes() {
        if (mWindow != null) {
            return mWindow.getAttributes().screenBrightness;
        }
        return 0;
    }

    /**
     * 设置亮度
     *
     * @param vol 亮度值
     */
    public void setBrightnes(float vol) {
        if (mWindow != null) {
            WindowManager.LayoutParams attributes = mWindow.getAttributes();
            if (vol > 1) {
                vol = 1;
            }
            if (vol <= 0) {
                vol = 0;
            }
            attributes.screenBrightness = vol;
            mWindow.setAttributes(attributes);
        }
    }
}
