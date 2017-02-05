package com.wzx.sticky.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import com.wzx.sticky.PinnedHeaderTouchHelper;

public class PinnedDoubleHeaderListView extends PinnedHeaderListView {

    private static final String TAG = PinnedDoubleHeaderListView.class.getSimpleName();

    private View mMainHeaderView;
    private int mEndPosition = -1;

    protected int mPinnedMainHeaderMarginTop = 0;

    private PinnedHeaderTouchHelper mPinnedHeaderTouchHelper;

    public PinnedDoubleHeaderListView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }
    public PinnedDoubleHeaderListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
        // TODO Auto-generated constructor stub
    }
    public PinnedDoubleHeaderListView(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        mPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this);
    }

    protected void setPinnedMainHeaderMarginTop(int top) {
        mPinnedMainHeaderMarginTop = top;
        if (mMainHeaderView == null) {
            mPinnedHeaderMarginTop = top;
        }
    }

    private int getHeaderViewHeight() {
        if (mMainHeaderView != null) {
            ensurePinnedHeaderLayout(mMainHeaderView, false);
            ensureAttachedToWindow(mMainHeaderView);
            return mMainHeaderView.getHeight();
        }
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        if (mMainHeaderView != null) {
            ensurePinnedHeaderLayout(mMainHeaderView, true);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);
        if (mMainHeaderView != null && mPinnedHeaderMarginTop > 0) {
            int saveCount = canvas.save();
            canvas.translate(0, mPinnedHeaderMarginTop - getHeaderViewHeight());
            canvas.clipRect(0, 0, getWidth(),
                    getHeaderViewHeight());

            mMainHeaderView.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    protected void internalOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int firstVisiblePositionForAdapter = firstVisibleItem - getHeaderViewsCount();

        if (mPinnedMainHeaderMarginTop > 0) {
            int N = getChildCount();
            for (int i = 0; i < N; i++) {
                View child = getChildAt(i);
                if (child.getBottom() <= mPinnedMainHeaderMarginTop) {
                    ++firstVisiblePositionForAdapter;
                } else {
                    break;
                }
            }
        }

        mMainHeaderView = getDoublePinnedHeaderView(firstVisiblePositionForAdapter);
        if (mMainHeaderView != null) {
            int endPosition = mEndPosition;
            if (endPosition == -1) {
                endPosition = getHeaderViewsCount() + mAdapter.getCount() - getFooterViewsCount();
            }
            if (firstVisibleItem > endPosition || getChildCount() == 0) {
                mPinnedHeaderMarginTop = 0;
            } else if (firstVisibleItem + visibleItemCount - 1 >= endPosition) {
                View end = getChildAt(endPosition - firstVisibleItem);
                mPinnedHeaderMarginTop = Math.min(mPinnedMainHeaderMarginTop + getHeaderViewHeight(), end.getBottom());
            } else {
                mPinnedHeaderMarginTop = mPinnedMainHeaderMarginTop + getHeaderViewHeight();
            }
        }

        super.internalOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    private View getDoublePinnedHeaderView(int position) {
        if (mAdapter == null || mAdapter.getCount() == 0 || position < 0 || position >= mAdapter.getCount()) {
            return null;
        }
        View view = mAdapter.getDoublePinnedHeaderView(position, this);
        if (view != null) {
            ensurePinnedHeaderLayout(view, false);
            ensureAttachedToWindow(view);
        }
        return view;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return mPinnedHeaderTouchHelper.shouldDelayChildPressedState() && super.shouldDelayChildPressedState();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (mMainHeaderView == null || mPinnedHeaderMarginTop <= 0) {
            return super.dispatchTouchEvent(ev);
        }

        Rect rect = new Rect(0, mPinnedMainHeaderMarginTop, mMainHeaderView.getWidth(),
                mPinnedHeaderMarginTop);
        if (mPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mMainHeaderView, rect, ev, 0,
                getHeaderViewHeight() - mPinnedHeaderMarginTop)) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

}
