package com.example.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.R;
import com.example.utils.DpUtils;

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
        extends View {
    private Context mContext;
    private int mRoundWidth = 10;    //圆线宽
    private int mSpacing = 10;       //间距
    private Paint mRoundBgPaint;
    private Paint mRoundPaint;
    private int mDiam;          //图片宽或高的最大值
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private RectF mRecF;
    private float mSweepAngle = 0;

    public SoundProgressView(Context context) {
        this(context, null);
    }

    public SoundProgressView(Context context, @Nullable AttributeSet attrs) {
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
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        mDiam = Math.max(width, height);

        mRecF = new RectF();


    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int cx = getWidth() / 2;
        int cy = cx;
        int temp = mRoundWidth / 2;
        int radius = mDiam / 2 + mSpacing / 2;
        canvas.drawCircle(cx, cy, radius, mRoundBgPaint);

        mRecF.set(temp, temp, getWidth() - temp, getHeight() - temp);
        canvas.drawArc(mRecF, 0, mSweepAngle, false, mRoundPaint);

        canvas.drawBitmap(mBitmap, cx - mDiam / 2, cx - mDiam / 2, mBitmapPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            //没有指定数值的宽度
            int width = mDiam + mRoundWidth + mSpacing;
            setMeasuredDimension(width, width);
        }
    }

    /**
     * 设置声音
     *
     * @param maxVolume    最大音量或亮度
     * @param currentVolum 当前音量或亮度
     */
    public void setVolume(int maxVolume, int currentVolum) {
        float rate = (float) currentVolum / maxVolume;
        mSweepAngle = rate * 360;
        postInvalidate();
    }

    /**
     * 设置亮度
     */
    public void setBrightnes(float brightnes) {
        mSweepAngle = brightnes * 360;
        postInvalidate();
    }

    /**
     * 设置图标
     *
     * @param res 资源ID
     */
    public void setIcon(int res) {
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
        postInvalidate();
    }
}
