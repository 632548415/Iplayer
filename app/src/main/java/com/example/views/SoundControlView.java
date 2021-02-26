package com.example.views;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.R;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   SoundControlView
 *  @创建者:   Administrator
 *  @创建时间:  2021/1/16 0016 0:26
 *  @描述：    声音和亮度的控制
 */
public class SoundControlView extends RelativeLayout {

    public SoundControlView(Context context) {
        this(context,null);
    }

    public SoundControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View              view         = View.inflate(context, R.layout.view_sound_control,this);
        SoundProgressView progressView = view.findViewById(R.id.progress_by_sound);
        AudioManager      am           = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            int streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
            int streamVolume    = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
        }
    }

}
