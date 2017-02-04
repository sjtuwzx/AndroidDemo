package com.wzx.android.demo.scrolllayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by wangzhenxing on 17/2/3.
 */

public class ScrollLinearLayout extends ViewGroup {

    private View mTopView;
    private int mMaxScrollY;

    private int mTouchSlop;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mMotionX;
    private float mMotionY;

    private boolean mDisallowIntercept;

    private int mActivePointerId = INVALID_POINTER;
    private static final int INVALID_POINTER = -1;

    private ValueAnimator mAnimator;

    public ScrollLinearLayout(Context context) {
        this(context, null);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTopView = null;
        mMaxScrollY = 0;

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (i == 0) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);

                mTopView = child;
                mMaxScrollY = child.getMeasuredHeight();
            } else {
                measureChild(child, widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, heightMode));
                height = Math.max(0, height - child.getMeasuredHeight());
            }

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            top += child.getMeasuredHeight();
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(ev);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                final int pointerIndex = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(pointerIndex);
                mMotionX = ev.getX(mActivePointerId);
                mMotionY = ev.getY(mActivePointerId);

                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(ev);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    float scrollY = internalGetScrollY();
                    float targetScrollY;
                    if (scrollY > mMaxScrollY / 2) {
                        targetScrollY = mMaxScrollY;
                    } else {
                        targetScrollY = 0;
                    }
                    smoothScrollTo(targetScrollY);
                }
                break;
            default:
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void onTouchDown(MotionEvent ev) {
        interceptDescendantTouchEvent(this, false);
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mIsBeingDragged = false;
        mDisallowIntercept = false;

        mActivePointerId = ev.getPointerId(0);
        mInitialMotionX = ev.getX();
        mInitialMotionY = ev.getY();
        mMotionX = mInitialMotionX;
        mMotionY = mInitialMotionY;

        if (isViewUnder(mTopView, mMotionX, mMotionY)) {
            mIsUnableToDrag = true;
        } else {
            mIsUnableToDrag = false;
        }
    }

    private void onTouchMove(MotionEvent ev) {
        int pointerIndex = ev.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1) {
            pointerIndex = 0;
            mActivePointerId = ev.getPointerId(pointerIndex);
        }
        determineDrag(ev);
        final float x = ev.getX(pointerIndex);
        final float y = ev.getY(pointerIndex);

        if (mIsBeingDragged) {
            final float dy = y - mMotionY;
            float scrollY = internalGetScrollY();
            if (dy > 0 && scrollY > 0) {
                scrollBy(Math.max(-dy, -scrollY));
            } else if (dy < 0 && scrollY < mMaxScrollY) {
                scrollBy(Math.min(-dy, mMaxScrollY - scrollY));
            }
        }
        mMotionX = x;
        mMotionY = y;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mMotionX = (int) ev.getX(newPointerIndex);
            mMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void determineDrag(MotionEvent ev) {
        if (!mDisallowIntercept && !mIsUnableToDrag && !mIsBeingDragged) {
            final float y = ev.getY(mActivePointerId);
            final float ady = Math.abs(y - mInitialMotionY);
            if (ady > mTouchSlop) {
                mIsBeingDragged = true;
                interceptDescendantTouchEvent(this, true);
            }
        }
    }

    private static boolean isViewUnder(View view, float x, float y) {
        if (view == null) {
            return false;
        }
        float translationX = view.getTranslationX();
        float translationY = view.getTranslationY();
        return x >= view.getLeft() + translationX
                && x < view.getRight() + translationX
                && y >= view.getTop() + translationY
                && y < view.getBottom() + translationY;
    }

    private void scrollBy(float dy) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setTranslationY(child.getTranslationY() - dy);
        }
    }

    private void scrollTo(float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setTranslationY(-y);
        }
    }

    private float internalGetScrollY() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            return -child.getTranslationY();
        }
        return 0;
    }

    public void smoothScrollTo(float y) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        float scrollY = internalGetScrollY();
        mAnimator = ValueAnimator.ofFloat(scrollY, y);
        if (scrollY >= y) {
            mAnimator.setInterpolator(new DecelerateInterpolator(2f));
        } else {
            mAnimator.setInterpolator(new AccelerateInterpolator(2f));
        }
        mAnimator.setDuration(200);
        mAnimator.addUpdateListener(mAnimatorUpdateListener);
        mAnimator.addListener(mAnimatorListener);
        mAnimator.start();
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (Float) animation.getAnimatedValue();
            scrollTo(value);
        }
    };

    private Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mAnimator = null;
        }
    };

    private static void interceptDescendantTouchEvent(ViewGroup ancestor, boolean intercept) {
        int childCount = ancestor.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = ancestor.getChildAt(i);
            if (child instanceof DescendantTouchController) {
                DescendantTouchController controller = (DescendantTouchController) child;
                controller.interceptTouchEvent(intercept);
            }
            if (child instanceof ViewGroup) {
                interceptDescendantTouchEvent((ViewGroup) child, intercept);
            }
        }
    }

    public interface DescendantTouchController {

        void interceptTouchEvent(boolean intercept);
    }
}
