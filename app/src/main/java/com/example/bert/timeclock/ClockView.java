package com.example.bert.timeclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Bert on 2016/2/1.
 */
public class ClockView extends View {

    public static final float START_ANGLE = -90;
    public static final int MESSAGE_CODE = 1;
    public MyHandler mMyHandler;
    public long SWEEP_ANGLE;
    private Paint mPaint;
    private Rect mRect;
    private RectF mRectf;
    private float mTop;
    private float mRadius;
    private float mBottom;
    private float mContourWidth;
    private long mTime;
    private float mTextSize;
    private int mMinute;
    private int mSecond;
    private int mMillisecond;
    private int mWidth;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mRectf = new RectF();
        mRect = new Rect();
        mMyHandler = new MyHandler(this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.timer);
        mRadius = typedArray.getDimension(R.styleable.timer_radius, 40);
        mTop = typedArray.getDimension(R.styleable.timer_top, 0);
        mBottom = typedArray.getDimension(R.styleable.timer_bottom, mRadius);
        mContourWidth = typedArray.getDimension(R.styleable.timer_contourWidth, 10);
        mTextSize = typedArray.getDimension(R.styleable.timer_textSize, 40);
        setTime(0);
        mWidth = 0;

    }

    // 将传入的时间解析为毫秒/秒/分钟
    public void setTime(long time) {
        mTime = time;
        mMinute = (int) mTime / 1000 / 60;
        mSecond = (int) (mTime % 60000 / 1000);
        mMillisecond = (int) (mTime % 1000) / 10;
        SWEEP_ANGLE = (long) (mTime * 1.0 / 60000  * 360);
        invalidate();
    }

    public MyHandler getMyHandler() {
        return mMyHandler;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mContourWidth);
        mRectf.set((getWidth() - mRadius) / 2, mTop,
                (getWidth() + mRadius) / 2, mTop + mBottom);

        // 画出时钟底盘
        mPaint.setColor(Color.GRAY);
        canvas.drawOval(mRectf, mPaint);

        // 画出时钟进度
        mPaint.setColor(Color.RED);
        canvas.drawArc(mRectf, START_ANGLE, SWEEP_ANGLE, false, mPaint);


        // 画出时间，在秒/毫秒为0的时候，以“00”显示
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(0);
        mPaint.setTextSize(mTextSize);
        String second = "" + (mSecond < 10 ? "0" + mSecond : mSecond);
        mPaint.getTextBounds(second, 0, second.length(), mRect);
        mWidth = mWidth == 0 ? mRect.width() : mWidth;
        canvas.drawText(second, getWidth() / 2 - mWidth / 2, getHeight() / 2 + mRect.height() / 2, mPaint);

        //画圈里面的分
        String minute = "" + mMinute;
        mPaint.getTextBounds(minute, 0, minute.length(), mRect);
        canvas.drawText(minute, getWidth() / 2 - mRect.width() - mWidth, getHeight() / 2 + mRect.height() / 2, mPaint);

        //画圈里面的毫秒
        String milliTime = "  " + (mMillisecond < 10 ? "0" + mMillisecond : mMillisecond);
        mPaint.setTextSize(mTextSize / 2);//设置毫秒的字体大小
        canvas.drawText(milliTime, getWidth() / 2 + mWidth / 2, getHeight() / 2 + mRect.height() / 2, mPaint);

    }


    public static class MyHandler extends Handler {

        public final WeakReference<ClockView> mClockViewWeakReference;

        public MyHandler(ClockView view){
            mClockViewWeakReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ClockView clockView = mClockViewWeakReference.get();

            switch (msg.what){
                case MESSAGE_CODE:
                    clockView.setTime(msg.getData().getInt("Time"));
                    break;
            }
        }
    }

}
