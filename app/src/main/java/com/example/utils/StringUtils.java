package com.example.utils;

import java.util.Formatter;
import java.util.Locale;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.utils
 *  @文件名:   StringUtils
 *  @创建者:   Administrator
 *  @创建时间:  2021/1/17 0017 0:23
 *  @描述：    TODO
 */
public class StringUtils {
    //将长度转换为时间
    public static String lengthForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter     mFormatter     = new Formatter(mFormatBuilder, Locale.getDefault());


        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }

    }
}
