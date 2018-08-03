package com.tata.breathbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import me.drakeet.BreathingViewHelper;

public class MainActivity extends AppCompatActivity {

    BreathBoxView breathBoxView;
    private View topView;
    private ImageView imageView;
    private int left;
    private int top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topView = findViewById(R.id.topView);
        topView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                left = topView.getLeft();
                top = topView.getTop();
            }
        });
        breathBoxView = findViewById(R.id.breathboxView);
        breathBoxView.setRepeatCount(3);
        breathBoxView.setInterval(2000);
        breathBoxView.setSpreadHeight(30);
        breathBoxView.setOnAnimationListener(new BreathBoxView.OnAnimationListener() {
            @Override
            public void onAnimationEnd() {

            }
        });
        imageView = findViewById(R.id.imageView);
    }

    public void onAnimClick(View v) {
//        breathBoxView.start();

        startTranslationAnim(imageView, topView);
//        BreathingViewHelper.setBreathingBackgroundColor(
//                findViewById(R.id.button),
//                getResources().getColor(R.color.colorPrimary));
    }

    private static final String TAG = "MainActivity";

    /**
     * 开始位移消失动画
     */
    private void startTranslationAnim(final View sourceView, final View targetView) {
        final Point startPoint = new Point((int) targetView.getX(), (int) targetView.getY());
        Log.d(TAG, "startTranslationAnim: startPoint = " + startPoint.toString());
        final Point endPoint = new Point((int) sourceView.getX(), (int) sourceView.getY());
        Log.d(TAG, "startTranslationAnim: endPoint = " + endPoint.toString());

        ValueAnimator tranlationAnimator = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        tranlationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                sourceView.setVisibility(View.VISIBLE);
                sourceView.setAlpha(1f);
                sourceView.setScaleY(1.0f);
                sourceView.setScaleX(1.0f);
                sourceView.setTranslationX(endPoint.x);
                sourceView.setTranslationY(endPoint.y);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f, 0f);
                alphaAnimator.setDuration(2000);
                alphaAnimator.start();
                alphaAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        breathBoxView.start();
                        sourceView.setVisibility(View.GONE);

                    }
                });

            }
        });
        tranlationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = new BigDecimal(valueAnimator.getAnimatedFraction()).setScale(2, RoundingMode.UP).floatValue();

                Log.d(TAG, "onAnimationUpdate: fraction = " + fraction);
                Point point = (Point) valueAnimator.getAnimatedValue();
                sourceView.setTranslationX(-point.x);
                sourceView.setTranslationY(-point.y);
                Log.d(TAG, "onAnimationUpdate: point = " + point.toString());

                int sourceWidth = sourceView.getMeasuredWidth();
                int sourceHeight = sourceView.getMeasuredHeight();

                int targetWidth = targetView.getMeasuredWidth();
                int targetHeight = targetView.getMeasuredHeight();

                float factor = (float) targetWidth / sourceWidth;
                Log.d(TAG, "onAnimationUpdate: factor= " + factor);

                float scaleX = (sourceWidth - fraction * (sourceWidth - targetWidth)) / sourceWidth;
                float scaleY = (sourceHeight - fraction * (sourceHeight - targetHeight)) / sourceHeight;


                Log.d(TAG, "onAnimationUpdate: scaleX = " + scaleX + "， scaleY = " + scaleY);

                sourceView.setScaleX(scaleX);
                sourceView.setScaleY(scaleY);
            }
        });

        tranlationAnimator.setDuration(450);
        tranlationAnimator.setInterpolator(new AccelerateInterpolator());
        tranlationAnimator.start();
    }

    private class PointEvaluator implements TypeEvaluator<Point> {

        @Override
        public Point evaluate(float fraction, Point startPoint, Point endPoint) {
            int x = (int) (startPoint.x + fraction * (endPoint.x - startPoint.x));
            int y = (int) (startPoint.y + fraction * (endPoint.y - startPoint.y));
            return new Point(x, y);
        }
    }
}
