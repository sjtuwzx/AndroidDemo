package com.wzx.android.demo.pinnedHeader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.MotionEventCompat;
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
public class PinnedHeaderScrollView extends ScrollView {

    private ArrayMap<View, View> mPinnedHeaderViewPairs = new ArrayMap<View, View>(8);
    private ArrayList<View> mSortedPinnedHeaderViews = new ArrayList<View>(8);
    private ArrayMap<View, Integer> mDescendantViewTopCache = new ArrayMap<View, Integer>(8);
    private ArrayMap<View, Integer> mPinnedHeaderCloseBottomCache = new ArrayMap<View, Integer>(8);

    private View mPinnedHeaderView;
    protected int mPinnedHeaderMarginTop = 0;
    private int mPinnedHeaderOffset = 0;

    private PinnedHeaderTouchHelper mPinnedHeaderTouchHelper;

    public PinnedHeaderScrollView(Context context) {
        this(context, null);
    }

    public PinnedHeaderScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedHeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this);
    }

    public void addPinnedHeader(View pinnedHeaderView, View closeView) {
        mPinnedHeaderViewPairs.put(pinnedHeaderView, closeView);
        requestLayout();
        invalidate();
    }

    public void removePinnedHeader(View pinnedHeaderView) {
        pinnedHeaderView.setVisibility(VISIBLE);
        mPinnedHeaderViewPairs.remove(pinnedHeaderView);

        requestLayout();
        invalidate();
    }

    public void reset() {
        mPinnedHeaderViewPairs.clear();

        requestLayout();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mDescendantViewTopCache.clear();
        mPinnedHeaderCloseBottomCache.clear();

        mSortedPinnedHeaderViews.clear();
        Set<View> mPinnedHeaderViewSet = mPinnedHeaderViewPairs.keySet();
        mSortedPinnedHeaderViews.addAll(mPinnedHeaderViewSet);
        Collections.sort(mSortedPinnedHeaderViews, mPinnedHeaderViewComparator);
    }

    private Comparator<View> mPinnedHeaderViewComparator = new Comparator<View>() {
        @Override
        public int compare(View lhs, View rhs) {
            int lTop = getDescendantTop(lhs);
            int rTop = getDescendantTop(rhs);

            return lTop - rTop;
        }
    };

    /**
     * 获取后裔控件相对于自身的top
     * @param descendant
     * @return
     */
    protected int getDescendantTop(View descendant) {
        if (descendant == null) {
            return -1;
        }
        if (mDescendantViewTopCache.containsKey(descendant)) {
            return mDescendantViewTopCache.get(descendant);
        }

        int top = 0;
        View originDescendant = descendant;
        ViewParent parent = descendant.getParent();
        while (parent instanceof ViewGroup) {
            top += descendant.getTop();
            if (parent == this) {
                mDescendantViewTopCache.put(originDescendant, top);
                return top;
            } else {
                descendant = (View)parent;
                parent = descendant.getParent();
            }
        }

        return -1;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        determinePinnedHeader();
    }

    protected void determinePinnedHeader() {
        if (mPinnedHeaderView != null) {
            mPinnedHeaderView.setVisibility(View.VISIBLE);
            mPinnedHeaderView = null;
        }
        mPinnedHeaderView = findPinnedHeaderView();
        if (mPinnedHeaderView != null) {
            mPinnedHeaderOffset = calculatePinnedHeaderOffset();
            if (mPinnedHeaderOffset >= mPinnedHeaderView.getHeight()) {
                mPinnedHeaderView = null;
            }
        }

        if (mPinnedHeaderView != null) {
            mPinnedHeaderView.setVisibility(INVISIBLE);
        }

        invalidate();
    }

    private View findPinnedHeaderView() {
        int scrollY = getScrollY();

        for (int i = mSortedPinnedHeaderViews.size() - 1; i >= 0; i--) {
            View view = mSortedPinnedHeaderViews.get(i);
            int top = getDescendantTop(view);
            if (top >= 0 && scrollY + mPinnedHeaderMarginTop > top) {
                return view;
            }
        }
        return null;
    }

    private int calculatePinnedHeaderOffset() {
        int pinnedHeaderViewBottom = getDescendantTop(mPinnedHeaderView) + mPinnedHeaderView.getHeight();
        int closeBottom = fetchPinnedCloseBottom(mPinnedHeaderView);
        //若closeBottom在pinnedHeaderView底部上面,则无需置顶
        if (closeBottom <= pinnedHeaderViewBottom) {
            return Integer.MAX_VALUE;
        }
        int closeBottomWithScrollY = closeBottom - getScrollY();
        int pinnedHeaderOriginBottom = mPinnedHeaderMarginTop + mPinnedHeaderView.getHeight();
        if (pinnedHeaderOriginBottom > closeBottomWithScrollY) {
            return pinnedHeaderOriginBottom - closeBottomWithScrollY;
        }

        return 0;
    }

    private int fetchPinnedCloseBottom(View pinnedHeaderView) {
        if (mPinnedHeaderCloseBottomCache.containsKey(pinnedHeaderView)) {
            return mPinnedHeaderCloseBottomCache.get(pinnedHeaderView);
        }

        View closeView = mPinnedHeaderViewPairs.get(pinnedHeaderView);
        View nextPinnedHeaderView = findNextPinnedHeaderView(pinnedHeaderView);

        int closeViewBottom = closeView == null ? Integer.MAX_VALUE : getDescendantTop(closeView) + closeView.getHeight();
        int nextPinnedHeaderViewTop = nextPinnedHeaderView == null ? Integer.MAX_VALUE : getDescendantTop(nextPinnedHeaderView);

        int closeBottom = Math.min(closeViewBottom, nextPinnedHeaderViewTop);
        mPinnedHeaderCloseBottomCache.put(pinnedHeaderView, closeBottom);

        return closeBottom;

    }

    private View findNextPinnedHeaderView(View pinnedHeaderView) {
        int index = mSortedPinnedHeaderViews.indexOf(pinnedHeaderView);
        if (index < mSortedPinnedHeaderViews.size() - 1) {
            return mSortedPinnedHeaderViews.get(index + 1);
        }
        return null;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPinnedHeaderView != null) {
            int saveCount = canvas.save();
            int scrollY  = getScrollY();
            canvas.translate(0, mPinnedHeaderMarginTop - mPinnedHeaderOffset + scrollY);
            canvas.clipRect(0, 0, getWidth(), mPinnedHeaderView.getHeight());
            mPinnedHeaderView.draw(canvas);

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
        if (mPinnedHeaderView == null) {
            return dispatchTouchEventSafely(ev);
        }
        Rect rect = new Rect(0, mPinnedHeaderMarginTop - mPinnedHeaderOffset, mPinnedHeaderView.getWidth(),
                mPinnedHeaderMarginTop - mPinnedHeaderOffset + mPinnedHeaderView.getHeight());
        if (mPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mPinnedHeaderView, rect,
                ev, 0, mPinnedHeaderOffset - mPinnedHeaderMarginTop)) {
            return true;
        } else {
            return dispatchTouchEventSafely(ev);
        }

    }

    private boolean mTouchDownScroll = false;

    private boolean dispatchTouchEventSafely(MotionEvent ev) {
        int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            mTouchDownScroll = true;
        } else if (mTouchDownScroll && (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            mTouchDownScroll = false;
            return super.dispatchTouchEvent(ev);
        }
        if (mTouchDownScroll) {
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }
}
