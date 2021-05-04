package example.com.smediaplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
    public static String lengthForTime(long timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter     mFormatter     = new Formatter(mFormatBuilder, Locale.getDefault());


        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                             .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds)
                             .toString();
        }
    }


    //Context 转换 Activity
    public static Activity transActivity(Context context) {
        if (context == null) { return null; }
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return transActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    //Context 转换 Activity
    public static AppCompatActivity getAppCompatActivity(Context context) {
        if (context == null) { return null; }
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextWrapper) {
            return getAppCompatActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * 隐藏ActionBar和状态栏
     */
    public static void hideActionBar(Context context) {
        AppCompatActivity appCompatActivity = getAppCompatActivity(context);
        if (appCompatActivity != null) {
            ActionBar supportActionBar = appCompatActivity.getSupportActionBar();
            if (supportActionBar != null && supportActionBar.isShowing()) {
                supportActionBar.hide();
                Activity activity = transActivity(context);
                if (activity == null) { return; }
                Window window = activity.getWindow();
                if (window == null) { return; }
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

            }
        }
    }

    /**
     * 显示ActionBar和状态栏
     * @param context
     */
    public static void showActionBar(Context context) {
        AppCompatActivity appCompatActivity = getAppCompatActivity(context);
        if (appCompatActivity != null) {
            ActionBar supportActionBar = appCompatActivity.getSupportActionBar();
            if (supportActionBar != null && !supportActionBar.isShowing()) {
                supportActionBar.show();
            }
            Activity activity = transActivity(context);
            if (activity == null) { return; }
            Window window = activity.getWindow();
            if (window == null) { return; }
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


    }

    public static boolean isVideo(String path){
        if (TextUtils.isEmpty(path))
            return false;
        int index = path.lastIndexOf(".");
        String videoType = path.substring(index);
        String reg = "(mp4|flv|avi|rm|rmvb|wmv)";
        Pattern p = Pattern.compile(reg);
        return p.matcher(videoType).find();
    }

}
