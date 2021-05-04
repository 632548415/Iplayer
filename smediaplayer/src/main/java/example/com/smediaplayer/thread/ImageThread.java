package example.com.smediaplayer.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    example.com.smediaplayer.thread
 *  @文件名:   ImageThread
 *  @创建者:   Administrator
 *  @创建时间:  2021/5/4 0004 21:48
 *  @描述：    TODO
 */
public class ImageThread extends Thread {
    private String mPath;
    private Handler mHandler;
    private int mConnectTime = 6*1000;
    private int mReadTime = 6*1000;
    private int mResponseCode = 200;
    public ImageThread(Handler handler, String path) {
        mHandler = handler;
        mPath = path;
    }

    @Override
    public void run() {
        try {
            URL               url        = new URL(mPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(mConnectTime);
            connection.setReadTimeout(mReadTime);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == mResponseCode){
                InputStream inputStream = connection.getInputStream();
                Bitmap      bitmap      = BitmapFactory.decodeStream(inputStream);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }else {
               mHandler.sendEmptyMessage(responseCode);
            }
        } catch (IOException e) {
            mHandler.sendEmptyMessage(1);
        }
    }
}
