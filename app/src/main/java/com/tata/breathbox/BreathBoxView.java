package com.tata.breathbox;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Map;

/**
 * Description : 会呼吸的盒子<br>
 * Author : Terry Liu <br>
 * Date : 2018/8/1 下午3:32
 */
public class BreathBoxView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {

    public BreathBoxView setSpreadHeight(int spreadHeight) {
        this.mSpreadHeight = spreadHeight;
        return this;
    }

    /**
     * 扩散的高度
     */
    private int mSpreadHeight = 10;

    /**
     * 扩散的颜色
     */
    private int mSpreadColor = Color.parseColor("#FFA4FE");

    public BreathBoxView setSpreadColor(int spreadColor) {
        this.mSpreadColor = mSpreadColor;
        return this;
    }

    Paint mPaint = new Paint();

    private android.os.Handler handler;
    private ValueAnimator valueAnimator;
    private ValueAnimator alphaAnimator;

    private boolean isBreathing = false;
    private long interval = 2000;
    private int currentBreathCount = 0;

    /**
     * 呼吸次数
     */
    private int repeatCount = UN_LIMITED_COUNT;

    public static final int UN_LIMITED_COUNT = -1;

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    private int circleX;
    private int circleY;

    private float mFraction = 0;


    public BreathBoxView(Context context) {
        super(context);

        init();
    }

    public BreathBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BreathBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setWillNotDraw(false);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mSpreadColor);
        handler = new android.os.Handler();

        valueAnimator = ValueAnimator.ofFloat(0, 1f).setDuration(interval);
        valueAnimator.addUpdateListener(this);

        setPadding(mSpreadHeight, mSpreadHeight, mSpreadHeight, mSpreadHeight);

    }

    private ValueAnimator.AnimatorUpdateListener getAlphaAnimatorListener() {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        };
    }

    public void start() {
        stop();
        handler.post(breathingRunnable);
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    private Runnable breathingRunnable = new Runnable() {
        @Override
        public void run() {
            if (repeatCount != UN_LIMITED_COUNT) {
                if (currentBreathCount >= repeatCount) {
                    stop();
                    if (onAnimationListener != null) {
                        onAnimationListener.onAnimationEnd();
                    }
                    isClear = true;
                    invalidate();
                    return;
                }
            }
            startBreathing();
            handler.postDelayed(this, interval + 100);
            currentBreathCount++;
        }
    };
    OnAnimationListener onAnimationListener;

    public OnAnimationListener getOnAnimationListener() {
        return onAnimationListener;
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.onAnimationListener = onAnimationListener;
    }

    public interface OnAnimationListener {
        void onAnimationEnd();
    }

    private void startBreathing() {
        isBreathing = true;
        valueAnimator.start();
    }

    public void stop() {
        isBreathing = false;
        currentBreathCount = 0;
        handler.removeCallbacks(breathingRunnable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circleX = w / 2;
        circleY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制扩散圆圈
        if (isBreathing) {
            mPaint.setAlpha((int) (255 * (1 - mFraction)));
            mPaint.setColor(mSpreadColor);
            int startRadius = circleX - mSpreadHeight;
            canvas.drawCircle(circleX, circleY, startRadius + mFraction * mSpreadHeight, mPaint);
        }

        //清除画布
        if (isClear) {
            mPaint = new Paint();
            mPaint.setColor(Color.TRANSPARENT);
            canvas.drawPaint(mPaint);
        }

    }

    private boolean isClear = false;

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        mFraction = (float) valueAnimator.getAnimatedValue();
        invalidate();
    }

    private static final String TAG = "BreathBoxView";
}
