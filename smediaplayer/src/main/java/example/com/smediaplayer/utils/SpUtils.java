package example.com.smediaplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.utils
 *  @文件名:   SpUtils
 *  @创建者:   Administrator
 *  @创建时间:  2021/3/14 0014 11:48
 *  @描述：    TODO
 */
public class SpUtils {
    private static final String NAME = "POSITION_DATA";
    private static final String TAG = "POSITION";
    public static void savePosition(Context context,long position){
        SharedPreferences sp = context.getSharedPreferences(NAME,0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(TAG,position);
        edit.apply();
    }
    public static int getPosition(Context context){
        SharedPreferences sp = context.getSharedPreferences(NAME, 0);
        return (int) sp.getLong(TAG,0);
    }
}
