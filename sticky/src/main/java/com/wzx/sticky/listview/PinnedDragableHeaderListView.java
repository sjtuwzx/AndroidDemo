package com.wzx.sticky.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import com.wzx.sticky.PinnedHeaderTouchHelper;

public class PinnedDragableHeaderListView extends PinnedDoubleHeaderListView {

    private static final String TAG = PinnedDragableHeaderListView.class.getSimpleName();

    private static final int MAX_SETTLE_DURATION = 250; // ms

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

    private View mDragableHeaderView;

    private int mStartPinnedPosition = 0;

    private PinnedHeaderTouchHelper mPinnedHeaderTouchHelper;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };

    public PinnedDragableHeaderListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();
    }

    public PinnedDragableHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initView();
    }

    public PinnedDragableHeaderListView(Context context, AttributeSet attrs,
                                        int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initView();
    }

    private void initView() {
        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mScroller = new Scroller(context, sInterpolator);
        mPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this);
    }

    public void setDragableHeaderView(View view, int startPinnedPosition) {
        mDragableHeaderView = view;
        mStartPinnedPosition = startPinnedPosition;
        invalidate();
    }

    private int getDragableHeaderViewHeight() {
        if (mDragableHeaderView != null) {
            ensurePinnedHeaderLayout(mDragableHeaderView, false);
            ensureAttachedToWindow(mDragableHeaderView);
            return mDragableHeaderView.getHeight();
        }
        return 0;
    }

    public int getDragableHeaderVisibleHeight() {
        return mPinnedMainHeaderMarginTop;
    }

    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
        if (mDragableHeaderView != null) {
            ensureAttachedToWindow(mDragableHeaderView);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDragableHeaderView != null) {
            ensurePinnedHeaderLayout(mDragableHeaderView, true);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);
        if (mDragableHeaderView != null && mPinnedMainHeaderMarginTop > 0) {
            int saveCount = canvas.save();
            canvas.translate(0, mPinnedMainHeaderMarginTop - getDragableHeaderViewHeight());
            canvas.clipRect(0, 0, getWidth(),
                    getDragableHeaderViewHeight());

            mDragableHeaderView.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    protected void internalOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == mStartPinnedPosition - 1) {
            View child = getChildAt(0);
            if (child.getTop() >= 0) {
                setPinnedMainHeaderMarginTop(0);
                endDrag();
            }
        } else if (firstVisibleItem < mStartPinnedPosition - 1) {
            setPinnedMainHeaderMarginTop(0);
            endDrag();
        }
        super.internalOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return mPinnedHeaderTouchHelper.shouldDelayChildPressedState() && super.shouldDelayChildPressedState();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (mDragableHeaderView == null || mPinnedMainHeaderMarginTop <= 0
                || mPinnedMainHeaderMarginTop != mDragableHeaderView.getHeight()) {
            return super.dispatchTouchEvent(ev);
        }

        Rect rect = new Rect(0, 0, mDragableHeaderView.getWidth(),
                mDragableHeaderView.getHeight());
        if (mPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mDragableHeaderView, rect, ev, 0, 0)) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    protected boolean dispatchListViewTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (mScrolling) {
            return super.dispatchListViewTouchEvent(ev);
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
                    determineDrag(ev);
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

                    int oldPinnedHeaderMarginTop = mPinnedMainHeaderMarginTop;
                    setPinnedMainHeaderMarginTop(Math.max(0, Math.min(getDragableHeaderViewHeight(), mPinnedMainHeaderMarginTop + (int) deltaY)));

                    if (oldPinnedHeaderMarginTop != mPinnedMainHeaderMarginTop) {
                        refreshPinnedHeaderLocation();
                    }
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
                    final float pageOffset =  ((float)mPinnedMainHeaderMarginTop) / getDragableHeaderViewHeight();
                    int headerMarginTop = 0;
                    if (mActivePointerId != INVALID_POINTER) {
                        headerMarginTop = determineTargetHeaderVisibleHeight(pageOffset, initialVelocity);
                    }
                    setPinnedHeaderMarginTopInternal(headerMarginTop, initialVelocity);
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    final float pageOffset =  ((float)mPinnedMainHeaderMarginTop) / getDragableHeaderViewHeight();
                    int headerMarginTop = 0;
                    if (mActivePointerId != INVALID_POINTER) {
                        headerMarginTop = determineTargetHeaderVisibleHeight(pageOffset, 0);
                    }
                    setPinnedHeaderMarginTopInternal(headerMarginTop, 0);
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
        return super.dispatchListViewTouchEvent(ev);
    }

    private void determineDrag(MotionEvent ev) {
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
            if (getFirstVisiblePosition() < mStartPinnedPosition && (dy > 0 || mPinnedMainHeaderMarginTop <= 0)) {

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

    private int determineTargetHeaderVisibleHeight(float dragOffset, int velocity) {
        int targetHeaderVisibleHeight = 0;
        if (Math.abs(velocity) > mMinimumVelocity) {
            if (velocity > 0) {
                targetHeaderVisibleHeight = getDragableHeaderViewHeight();
            } else if (velocity < 0){
                targetHeaderVisibleHeight = 0;
            }
        } else {
            targetHeaderVisibleHeight = dragOffset < 0.5f ? 0 : getDragableHeaderViewHeight();
        }
        return targetHeaderVisibleHeight;
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

    private void setPinnedHeaderMarginTopInternal(int top, int velocity) {
        int dy = top - mPinnedMainHeaderMarginTop;
        if (dy == 0) {
            return;
        }

        mScrolling = true;

        int duration = 0;
        velocity = Math.abs(velocity);
        final float distance = top - mPinnedMainHeaderMarginTop;
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance) / velocity);
        } else {
            duration = MAX_SETTLE_DURATION;
        }
        duration = Math.min(duration, MAX_SETTLE_DURATION);

        mScroller.startScroll(0, mPinnedMainHeaderMarginTop, 0, dy, duration);
    }

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        super.computeScroll();
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {

                setPinnedMainHeaderMarginTop(mScroller.getCurrY());
                refreshPinnedHeaderLocation();

                // Keep on drawing until the animation has finished.
                invalidate();
                return;
            }
        }

        // Done with scroll, clean up state.
        completeScroll();
    }

    private void completeScroll() {
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            mScroller.abortAnimation();
            setPinnedMainHeaderMarginTop(mScroller.getCurrY());
            refreshPinnedHeaderLocation();
        }
        mScrolling = false;
    }

    private void refreshPinnedHeaderLocation() {
        ListAdapter adapter = getAdapter();
        int totalItemCount = adapter == null ? 0 : adapter.getCount();
        super.internalOnScroll(this, getFirstVisiblePosition(), getChildCount(), totalItemCount);
        invalidate();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // TODO Auto-generated method stub
        SavedState ss = (SavedState)super.onSaveInstanceState();
        if (mScrolling) {
            ss.mPinnedHeaderMarginTop = mScroller.getFinalY();
        }
        return ss;
    }

    @Override
    protected void setPinnedMainHeaderMarginTop(int top) {
        // TODO Auto-generated method stub
        int firstVisibleItem = getFirstVisiblePosition();
        if (firstVisibleItem < mStartPinnedPosition - 1) {
            top = 0;
        }
        super.setPinnedMainHeaderMarginTop(top);
        if (mPinnedMainHeaderMarginTop == 0) {
            showView(mDragableHeaderView);
        } else {
            hideView(mDragableHeaderView);
        }
    }

}
