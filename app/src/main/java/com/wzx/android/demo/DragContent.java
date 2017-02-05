package com.wzx.android.demo;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by wang_zx on 2015/5/19.
 */
public class DragContent extends FrameLayout {

    private static final String TAG = DragContent.class.getSimpleName();

    private static final int MAX_SETTLE_DURATION = 300; // ms

    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private int mTouchSlop;
    /**
     * Position of the last motion event.
     */
    private float mLastMotionX;
    private float mLastMotionY;
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    protected int mActivePointerId = INVALID_POINTER;
    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    /**
     * Determines speed during touch scrolling
     */
    protected VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    protected int mMaximumVelocity;

    private boolean mScrolling;
    private Scroller mScroller;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };


    public DragContent(Context context) {
        this(context, null);
    }

    public DragContent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mScroller = new Scroller(context, sInterpolator);
    }

    boolean onParentTouchEvent(int scrollY, MotionEvent ev) {
        Log.i(TAG, String.format("[%d, %.2f, %.2f]", scrollY, ev.getX(), ev.getY()));
        if (mScrolling) {
            return true;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        int action = ev.getAction();
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
                completeScroll();

                // Remember where the motion event started
                int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                mLastMotionX = MotionEventCompat.getX(ev, index);
                mLastMotionY = MotionEventCompat.getY(ev, index);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    determineDrag(scrollY, ev);
                    if (mIsUnableToDrag)
                        break;
                }
                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    final int activePointerIndex = getPointerIndex(ev, mActivePointerId);
                    if (mActivePointerId == INVALID_POINTER)
                        break;
                    final float y = MotionEventCompat.getY(ev, activePointerIndex);
                    final float deltaY = y - mLastMotionY;
                    mLastMotionY = y;

                    int height = getHeight();
                    height += (int) deltaY;
                    setMinimumHeight(height);
                   /* mPinnedHeaderMarginTop = Math.max(0, Math.min(getDragableHeaderViewHeight(), mPinnedHeaderMarginTop + (int) deltaY));

                    if (oldPinnedHeaderMarginTop != mPinnedHeaderMarginTop) {
                        refreshPinnedHeaderLocation();
                    }*/
                    // Don't lose the rounded component
                    mLastMotionY -= deltaY - (int) deltaY;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) VelocityTrackerCompat.getYVelocity(
                            velocityTracker, mActivePointerId);
                   /* final float pageOffset =  ((float)mPinnedHeaderMarginTop) / getDragableHeaderViewHeight();
                    int headerMarginTop = 0;
                    if (mActivePointerId != INVALID_POINTER) {
                        headerMarginTop = determineTargetHeaderVisibleHeight(pageOffset, initialVelocity);
                    }
                    setPinnedHeaderMarginTopInternal(headerMarginTop, initialVelocity);*/
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                  /*  final float pageOffset =  ((float)mPinnedHeaderMarginTop) / getDragableHeaderViewHeight();
                    int headerMarginTop = 0;
                    if (mActivePointerId != INVALID_POINTER) {
                        headerMarginTop = determineTargetHeaderVisibleHeight(pageOffset, 0);
                    }
                    setPinnedHeaderMarginTopInternal(headerMarginTop, 0);*/
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int indexx = MotionEventCompat.getActionIndex(ev);
                mLastMotionY = MotionEventCompat.getY(ev, indexx);
                mActivePointerId = MotionEventCompat.getPointerId(ev, indexx);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                int pointerIndex = getPointerIndex(ev, mActivePointerId);
                if (mActivePointerId == INVALID_POINTER)
                    break;
                mLastMotionY = MotionEventCompat.getY(ev, pointerIndex);
                break;


        }
        return false;
    }

    private void determineDrag(int scrollY, MotionEvent ev) {
        final int activePointerId = mActivePointerId;
        final int pointerIndex = getPointerIndex(ev, activePointerId);
        if (activePointerId == INVALID_POINTER || pointerIndex == INVALID_POINTER)
            return;
        final float x = MotionEventCompat.getX(ev, pointerIndex);
        final float dx = x - mLastMotionX;
        final float xDiff = Math.abs(dx);
        final float y = MotionEventCompat.getY(ev, pointerIndex);
        final float dy = y - mLastMotionY;
        final float yDiff = Math.abs(dy);
        if (yDiff > mTouchSlop && yDiff > xDiff) {
            if (scrollY > 0) {
                mIsUnableToDrag = true;
                return;
            }
            startDrag();
            mLastMotionX = x;
            mLastMotionY = y;
        } else if (xDiff > mTouchSlop) {
            mIsUnableToDrag = true;
        }
    }

    private void startDrag() {
        mIsBeingDragged = true;
    }

    private void endDrag() {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
        mActivePointerId = INVALID_POINTER;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    private void completeScroll() {
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            mScroller.abortAnimation();
           /* mPinnedHeaderMarginTop = mScroller.getCurrY();
            refreshPinnedHeaderLocation();*/
        }
        mScrolling = false;
      /*  if (mPinnedHeaderMarginTop == 0) {
            showView(mDragableHeaderView);
        } else {
            hideView(mDragableHeaderView);
        }*/
    }

    private int getPointerIndex(MotionEvent ev, int id) {
        int activePointerIndex = MotionEventCompat.findPointerIndex(ev, id);
        if (activePointerIndex == -1)
            mActivePointerId = INVALID_POINTER;
        return activePointerIndex;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }
}
