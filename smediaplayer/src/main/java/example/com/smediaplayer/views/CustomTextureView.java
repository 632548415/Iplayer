package example.com.smediaplayer.views;

import android.content.Context;
import android.view.TextureView;

import androidx.annotation.NonNull;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    example.com.smediaplayer.views
 *  @文件名:   CustomTextureView
 *  @创建者:   Administrator
 *  @创建时间:  2021/3/15 0015 21:55
 *  @描述：    自定义TextureView在全屏的时候设置视频正确大小保证内容不变形,不是全屏的时候自己控制大小
 */
public class CustomTextureView
        extends TextureView
{
    private int mVideoHeight;
    private int mVideoWidth;

    public CustomTextureView(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight;
            } else if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth;
            }
            setMeasuredDimension(width, height);
        }
    }

    public void inFullScreen(int videoWidth,int videoHeight) {
        mVideoHeight = videoHeight;
        mVideoWidth = videoWidth;
    }
}
