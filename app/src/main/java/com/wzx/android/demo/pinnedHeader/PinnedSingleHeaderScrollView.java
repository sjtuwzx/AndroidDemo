package com.wzx.android.demo.pinnedHeader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * Created by wangzhenxing on 16/4/22.
 */
public class PinnedSingleHeaderScrollView extends ScrollView {

    private View mPinnedHeaderView;
    private View mPinnedHeaderCloseView;

    private ArrayMap<View, View> mPinnedHeaderViewPairs = new ArrayMap<View, View>();
    private ArrayList<View> mSortedPinnedHeaderViews = new ArrayList<View>();

    private View mCurrentPinnedHeaderView;
    private int mPinnedHeaderOffset = 0;

    private PinnedHeaderTouchHelper mPinnedHeaderTouchHelper;

    public PinnedSingleHeaderScrollView(Context context) {
        this(context, null);
    }

    public PinnedSingleHeaderScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedSingleHeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this);
    }

    public void setPinnedHeaderView(View pinnedHeaderView, View pinnedHeaderCloseView) {
        mPinnedHeaderView = pinnedHeaderView;
        mPinnedHeaderCloseView = pinnedHeaderCloseView;

        requestLayout();
        invalidate();
    }

    public void reset() {
        setPinnedHeaderView(null, null);
    }

    public void addPinnedHeaderPair(View pinnedHeaderView, View closeView) {
        mPinnedHeaderViewPairs.put(pinnedHeaderView, closeView);
        requestLayout();
        invalidate();
    }

    public void removePinnedHeaderPair(View pinnedHeaderView) {
        mPinnedHeaderViewPairs.remove(pinnedHeaderView);

        requestLayout();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mSortedPinnedHeaderViews.clear();
        Set<View> mPinnedHeaderViewSet = mPinnedHeaderViewPairs.keySet();
        mSortedPinnedHeaderViews.addAll(mPinnedHeaderViewSet);
        Collections.sort(mSortedPinnedHeaderViews, sPinnedHeaderViewComparator);
    }

    private Comparator<View> sPinnedHeaderViewComparator = new Comparator<View>() {
        @Override
        public int compare(View lhs, View rhs) {
            int lTop = getDescendantTop(lhs);
            int rTop = getDescendantTop(rhs);

            return lTop - rTop;
        }
    };

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mCurrentPinnedHeaderView != null) {
            mCurrentPinnedHeaderView.setVisibility(View.VISIBLE);
            mCurrentPinnedHeaderView = null;
        }
        if (mPinnedHeaderView != null) {
            int pinnedHeaderViewTop = getDescendantTop(mPinnedHeaderView);
            if (pinnedHeaderViewTop >= 0) {
                int scrollY = getScrollY();
                if (scrollY > pinnedHeaderViewTop) {
                    mCurrentPinnedHeaderView = mPinnedHeaderView;

                    mPinnedHeaderOffset = calculatePinnedHeaderOffset(pinnedHeaderViewTop + mCurrentPinnedHeaderView.getHeight());
                    if (mPinnedHeaderOffset >= mCurrentPinnedHeaderView.getHeight()) {
                        mCurrentPinnedHeaderView = null;
                    }
                }
            }

        }

        if (mCurrentPinnedHeaderView != null) {
            mCurrentPinnedHeaderView.setVisibility(INVISIBLE);
        }

    }

    private View findPinnedHeaderView() {
        int scrollY = getScrollY();
        for (View view : mSortedPinnedHeaderViews) {
            int top = getDescendantTop(view);
            if (top >= 0 && scrollY > top) {
                return view;
            }
        }
        return null;
    }

    private int calculatePinnedHeaderOffset(int pinnedHeaderViewBottom) {
        int closeViewTop = getDescendantTop(mPinnedHeaderCloseView);
        if (closeViewTop >= 0) {
            int closeViewBottom = closeViewTop + mPinnedHeaderCloseView.getHeight();
            //若closeView底部在pinnedHeaderView底部上面,则无需置顶
            if (closeViewBottom <= pinnedHeaderViewBottom) {
                return Integer.MAX_VALUE;
            }
            int closeViewBottomInScroll = closeViewBottom - getScrollY();
            int pinnedHeaderViewHeight = mCurrentPinnedHeaderView.getHeight();
            if (pinnedHeaderViewHeight > closeViewBottomInScroll) {
                return pinnedHeaderViewHeight - closeViewBottomInScroll;
            }
        }

        return 0;
    }

    /**
     * 获取后裔控件相对于自身的top
     * @param descendant
     * @return
     */
    private int getDescendantTop(View descendant) {
        if (descendant == null) {
            return -1;
        }
        int top = 0;
        ViewParent parent = descendant.getParent();
        while (parent instanceof ViewGroup) {
            top += descendant.getTop();
            if (parent == this) {
                return top;
            } else {
                descendant = (View)parent;
                parent = descendant.getParent();
            }
        }

        return -1;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCurrentPinnedHeaderView != null) {
            int saveCount = canvas.save();
            int scrollY  = getScrollY();
            canvas.translate(0, -mPinnedHeaderOffset + scrollY);
            canvas.clipRect(0, 0, getWidth(), mCurrentPinnedHeaderView.getHeight());
            mCurrentPinnedHeaderView.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }

    @TargetApi(14)
    @Override
    public boolean shouldDelayChildPressedState() {
        return mPinnedHeaderTouchHelper.shouldDelayChildPressedState() && super.shouldDelayChildPressedState();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mCurrentPinnedHeaderView == null) {
            return super.dispatchTouchEvent(ev);
        }
        Rect rect = new Rect(0, -mPinnedHeaderOffset, mCurrentPinnedHeaderView.getWidth(),
                mCurrentPinnedHeaderView.getHeight() - mPinnedHeaderOffset);
        if (mPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mCurrentPinnedHeaderView, rect,
                ev, 0, mPinnedHeaderOffset)) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }

    }
}
