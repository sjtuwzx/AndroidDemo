package com.wzx.android.demo;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class AnimationListView extends ListView implements AnimatorListener, AnimatorUpdateListener {

    private boolean mAnimating = false;

    private float mLineAnimationFactor = -1f;

    public AnimationListView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public AnimationListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public AnimationListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        mPaint.setColor(0xFF69B5E9);
    }

    private Paint mPaint = new Paint();

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mLineAnimationFactor > 0) {
            canvas.drawRect(99, (getHeight() - mLineAnimationFactor * getHeight()) + 30, 100,
                    getHeight(), mPaint);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (mAnimating) {
            hideChildren();
        }
    }

    public void startAnimation() {
        mAnimating = true;
        mLineAnimationFactor = -1f;
        hideChildren();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1500);
        animator.addUpdateListener(this);
        animator.addListener(this);
        animator.start();
    }

    private void hideChildren() {
        int N = getChildCount();
        for (int i = 0; i <= N - 1; i++) {
            View child = getChildAt(i);
            child.setAlpha(0);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // TODO Auto-generated method stub
        float factor = animation.getAnimatedFraction();
        if (factor < 0.15f) {
            mLineAnimationFactor = factor / 0.15f;
            invalidate();
        } else {
            mLineAnimationFactor = factor > 0.8f ? -1f : 1f;

            factor = (factor - 0.15f) / 0.85f;
            int N = getChildCount();
            float df = 1.0f / (N + 2);
            for (int i = 0; i <= N - 1; i++) {
                View child = getChildAt(N - 1 - i);
                if (child instanceof AnimationChild) {
                    AnimationChild animationChild = (AnimationChild) child;
                    animationChild.onAnimation(Math.max(0f, Math.min(1.0f, (factor - df * i) / (3 * df))));
                }
            }
            invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return mAnimating || super.dispatchTouchEvent(ev);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        // TODO Auto-generated method stub
        mAnimating = false;
        mLineAnimationFactor = -1f;


    }

    @Override
    public void onAnimationCancel(Animator animation) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // TODO Auto-generated method stub

    }

    public static interface AnimationChild {

        void onAnimation(float factor);
    }

}
