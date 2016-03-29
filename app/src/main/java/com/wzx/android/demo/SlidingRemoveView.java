package com.wzx.android.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2016/2/3.
 */
public class SlidingRemoveView extends ViewGroup implements ValueAnimator.AnimatorUpdateListener {

    private static final int DEFAULT_REMOVE_ANIMATOR_DURATION = 250;

    private int mContainerViewID;
    private int mRemoveButtonID;

    private View mContainerView;
    private View mRemoveButton;

    private final ViewDragHelper mDragHelper;

    private int mHorizontalDragRange = 0;
    private int mScrollX = 0;

    private boolean mCanOpen = true;
    private boolean mIsOpened = false;

    private boolean mShowRemoveAnimator = true;
    private float mRemoveAnimatorFactor = 0.0f;

    private boolean mIsBeingDragged = false;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private int mRemoveAnimatorDuration;
    private ValueAnimator mRemoveAnimator;
    private OnViewRemoveListener mOnViewRemoveListener;

    public SlidingRemoveView(Context context) {
        this(context, null);
    }

    public SlidingRemoveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingRemoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SlidingRemoveView, defStyleAttr, 0);
        mContainerViewID = a.getResourceId(R.styleable.SlidingRemoveView_container_view_id,
                View.NO_ID);
        mRemoveButtonID = a.getResourceId(R.styleable.SlidingRemoveView_remove_button_id,
                View.NO_ID);

        mRemoveAnimatorDuration = a.getInt(R.styleable.SlidingRemoveView_remove_animator_duration, DEFAULT_REMOVE_ANIMATOR_DURATION);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mContainerView = findViewById(mContainerViewID);
        mRemoveButton = findViewById(mRemoveButtonID);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mContainerView != null) {
            measureView(mContainerView, widthMeasureSpec, heightMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (heightMode != MeasureSpec.EXACTLY) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(mContainerView.getMeasuredHeight(), MeasureSpec.EXACTLY);
            }
        }
        if (mRemoveButton != null) {
            measureView(mRemoveButton, widthMeasureSpec, heightMeasureSpec);
            mHorizontalDragRange = mRemoveButton.getMeasuredWidth();
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (heightSize * (1.0f - mRemoveAnimatorFactor)), heightMode);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private static void measureView(View view, int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams lp = view.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height);

        view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mContainerView != null) {
            mContainerView.layout(-mScrollX, 0, -mScrollX + mContainerView.getMeasuredWidth(), mContainerView.getMeasuredHeight());
        }
        if (mRemoveButton != null) {
            int width = getWidth();
            int height = getHeight();
            mRemoveButton.layout(width - mRemoveButton.getMeasuredWidth(), height - mRemoveButton.getMeasuredHeight(), width, height);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == mRemoveButton) {
            canvas.save();
            canvas.clipRect(getWidth() - mScrollX, 0, getWidth(), getHeight());
            boolean result = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return result;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mCanOpen && mIsBeingDragged && child == mContainerView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            mScrollX = -left;
            mContainerView.layout(left, top, left + mContainerView.getWidth(), top + mContainerView.getHeight());

            invalidate();

            if (mIsOpened && mScrollX <= 0) {
                mIsOpened = false;
                if (mRemoveButton != null) {
                    mRemoveButton.setOnClickListener(null);
                }
            } else if (!mIsOpened && mScrollX >= mHorizontalDragRange) {
                mIsOpened = true;
                if (mRemoveButton != null) {
                    mRemoveButton.setOnClickListener(mDeleteButtonClickListener);
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int left = 0;
            float minVelocity = mDragHelper.getMinVelocity() * 10;
            if (xvel <= -minVelocity
                    || (Math.abs(xvel) < minVelocity && mScrollX > mHorizontalDragRange / 2)) {
                left = -mHorizontalDragRange;
            }

            mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());

            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mHorizontalDragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int newLeft = Math.min(Math.max(left, -mHorizontalDragRange), 0);
            return newLeft;
        }

    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;

                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                if (adx * adx + ady * ady >= slop * slop) {
                    if (!mIsBeingDragged && ady > adx) {
                        ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        mIsBeingDragged = true;
                    }
                }
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;

                if (mDragHelper.isViewUnder(mContainerView, (int) x, (int) y) &&
                        mScrollX > 0 && mScrollX == mHorizontalDragRange) {
                    interceptTap = true;
                }
                break;
            }
        }

        final boolean interceptForDrag = mDragHelper.shouldInterceptTouchEvent(ev);

        return interceptForDrag || interceptTap;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        mDragHelper.processTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mScrollX > 0 && mScrollX == mHorizontalDragRange) {
                    final float x = ev.getX();
                    final float y = ev.getY();
                    final float dx = x - mInitialMotionX;
                    final float dy = y - mInitialMotionY;
                    final int slop = mDragHelper.getTouchSlop();
                    if (dx * dx + dy * dy < slop * slop &&
                            mDragHelper.isViewUnder(mContainerView, (int) x, (int) y)) {
                        close();
                        break;
                    }
                }
                break;
            }
        }

        return true;
    }

    boolean smoothSlideTo(float slideOffset) {
        float left = -mHorizontalDragRange * slideOffset;

        if (mDragHelper.smoothSlideViewTo(mContainerView, (int) left, mContainerView.getTop())) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    public void setShowRemoveAnimator(boolean showRemoveAnimator) {
        mShowRemoveAnimator = showRemoveAnimator;
    }

    public void setCanOpen(boolean canOpen) {
        mCanOpen = canOpen;
        requestLayout();
        invalidate();
    }

    public boolean isOpening() {
        return mScrollX > 0;
    }

    public void open() {
        smoothSlideTo(1f);
    }

    public void close() {
        smoothSlideTo(0.0f);
    }

    public void reset() {
        if (mRemoveAnimator != null) {
            mRemoveAnimator.cancel();
        }
        mScrollX = 0;
        mIsOpened = false;
        mRemoveAnimatorFactor = 0.0f;
        setAlpha(1.0f);
        setVisibility(VISIBLE);

        requestLayout();
        invalidate();
    }

    private OnClickListener mDeleteButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mRemoveButton.setOnClickListener(null);
            if (mShowRemoveAnimator) {
                performRemoveAnimator();
            } else if (mOnViewRemoveListener != null) {
                mOnViewRemoveListener.onRemoveFinish(SlidingRemoveView.this);
            }
        }
    };

    private void performRemoveAnimator() {
        mRemoveAnimator = ValueAnimator.ofFloat(0.0f);
        mRemoveAnimator.setDuration(mRemoveAnimatorDuration);
        mRemoveAnimator.addUpdateListener(this);
        mRemoveAnimator.addListener(mRemoveAnimatorListenerAdapter);
        mRemoveAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float factor = animation.getAnimatedFraction();
        setAlpha(1.0f - factor);
        mRemoveAnimatorFactor = factor;
        requestLayout();
        invalidate();
    }

    private AnimatorListenerAdapter mRemoveAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            setVisibility(GONE);
            mRemoveAnimator = null;
            if (mOnViewRemoveListener != null) {
                mOnViewRemoveListener.onRemoveFinish(SlidingRemoveView.this);
            }
        }
    };

    public interface OnViewRemoveListener {
        void onRemoveFinish(SlidingRemoveView slidingRemoveView);
    }

    public void setOnViewRemoveListener(OnViewRemoveListener listener) {
        mOnViewRemoveListener = listener;
    }

}
