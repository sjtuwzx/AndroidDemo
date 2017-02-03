package com.wzx.android.demo.pinnedHeader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wangzhenxing on 16/4/25.
 */
public class PinnedDoubleHeaderScrollView extends PinnedHeaderScrollView {

    private View mMainPinnedHeaderView;
    private View mMainHeaderCloseView;

    private PinnedHeaderTouchHelper mMainPinnedHeaderTouchHelper;

    public PinnedDoubleHeaderScrollView(Context context) {
        this(context, null);
    }

    public PinnedDoubleHeaderScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedDoubleHeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMainPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this);
    }

    public void setMainPinnedHeader(View pinnedHeaderView, View closeView) {
        mMainPinnedHeaderView = pinnedHeaderView;
        mMainHeaderCloseView = closeView;

        requestLayout();
        invalidate();
    }

    public void removeMainPinnedHeader() {
        setMainPinnedHeader(null, null);
    }

    @Override
    protected void determinePinnedHeader() {
        mPinnedHeaderMarginTop = 0;

        int pinnedHeaderTop = getDescendantTop(mMainPinnedHeaderView);
        int scrollY = getScrollY();
        if (pinnedHeaderTop >= 0 && scrollY > pinnedHeaderTop) {
            mPinnedHeaderMarginTop = fetchPinnedHeaderMaxVisibleHeight();
        }

        if (mPinnedHeaderMarginTop > 0) {
            mMainPinnedHeaderView.setVisibility(INVISIBLE);
        } else {
            mMainPinnedHeaderView.setVisibility(VISIBLE);
        }

        super.determinePinnedHeader();
    }

    private int fetchPinnedHeaderMaxVisibleHeight() {
        int pinnedHeaderBottom = getDescendantTop(mMainPinnedHeaderView) + mMainPinnedHeaderView.getHeight();
        //若closeBottom在mainPinnedHeaderView底部上面,则无需置顶
        int closeBottom = fetchMainPinnedCloseBottom();
        if (closeBottom <= pinnedHeaderBottom) {
            return 0;
        }

        return Math.min(mMainPinnedHeaderView.getHeight(), Math.max(0, closeBottom - getScrollY()));

    }

    private int fetchMainPinnedCloseBottom() {
        int closeViewTop = getDescendantTop(mMainHeaderCloseView);
        if (closeViewTop >= 0) {
            return closeViewTop + mMainHeaderCloseView.getHeight();
        }

        return Integer.MAX_VALUE;
    }

    @TargetApi(14)
    @Override
    public boolean shouldDelayChildPressedState() {
        return mMainPinnedHeaderTouchHelper.shouldDelayChildPressedState() && super.shouldDelayChildPressedState();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mPinnedHeaderMarginTop > 0) {
            Rect rect = new Rect(0, 0, mMainPinnedHeaderView.getWidth(), mPinnedHeaderMarginTop);
            if (mMainPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mMainPinnedHeaderView, rect,
                    ev, 0, mMainPinnedHeaderView.getHeight() - mPinnedHeaderMarginTop)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPinnedHeaderMarginTop > 0) {
            int saveCount = canvas.save();
            int scrollY  = getScrollY();
            canvas.translate(0, scrollY + mPinnedHeaderMarginTop - mMainPinnedHeaderView.getHeight());
            canvas.clipRect(0, 0, getWidth(), mMainPinnedHeaderView.getHeight());
            mMainPinnedHeaderView.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }
}
