package com.example.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.example.R;

import androidx.annotation.Nullable;

/*
 *  @项目名：  MyMediaPlay
 *  @包名：    com.example.views
 *  @文件名:   ProgressView
 *  @创建者:   Administrator
 *  @创建时间:  2020/12/26 0026 16:10
 *  @描述：    声音和亮度view
 */
public class SoundProgressView
        extends View
{
    private Context mContext;
    private int     mRoundWidth      = 8;    //圆宽
    private Paint   mRoundBgPaint;
    private Paint   mRoundPaint;
    private int     mRedius;
    private int     mSpacingByBitmap = 5;  //圆和图片的间距
    private int     mScreenWidth;
    private int     mScreenheight;
    private Bitmap  mBitmap;
    private Paint   mBitmapPaint;
    private RectF   mRecF;
    private float   mSweepAngle      = 0;

    public SoundProgressView(Context context) {
        this(context, null);
    }

    public SoundProgressView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mRoundBgPaint = new Paint();
        mRoundBgPaint.setStrokeWidth(mRoundWidth);
        mRoundBgPaint.setAntiAlias(true);
        mRoundBgPaint.setColor(mContext.getResources()
                                       .getColor(R.color.round_bg));
        mRoundBgPaint.setStyle(Paint.Style.STROKE);
        mRoundPaint = new Paint();
        mRoundPaint.setStrokeWidth(mRoundWidth);
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setColor(mContext.getResources()
                                     .getColor(R.color.white));
        mRoundPaint.setStyle(Paint.Style.STROKE);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.sound);
        int width  = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        mRedius = Math.max(width, height) + mSpacingByBitmap;

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager  wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            wm.getDefaultDisplay()
              .getMetrics(dm);
        }
        mScreenWidth = dm.widthPixels;
        mScreenheight = dm.heightPixels;

        mRecF = new RectF();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float cx = getWidth() / 2;
        float cy = cx;
        canvas.drawCircle(cx, cy, mRedius, mRoundBgPaint);

        mRecF.set(cx - mRedius, cy - mRedius, cx + mRedius, cy + mRedius);
        canvas.drawArc(mRecF, 0, mSweepAngle, false, mRoundBgPaint);

        canvas.drawBitmap(mBitmap, cx - mRedius / 2, cy - mRedius / 2, mBitmapPaint);
    }

    public void setAngle(int maxVolume, int currentVolum) {
        float rate = (float) currentVolum / maxVolume;
        mSweepAngle = rate * 360;
        postInvalidate();
    }
}
