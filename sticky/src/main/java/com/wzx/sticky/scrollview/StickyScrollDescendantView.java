package com.wzx.sticky.scrollview;

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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.wzx.sticky.PinnedHeaderTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * ScrollView的descendant控件,实现自身的后裔控件吸顶效果
 * Created by wangzhenxing on 17/1/19.
 */
public class StickyScrollDescendantView extends FrameLayout {

    private ArrayMap<View, View> mStickyViewPairs = new ArrayMap<>(8);
    private ArrayList<View> mSortedStickyViews = new ArrayList<>(8);
    private ArrayMap<View, Integer> mDescendantViewTopCache = new ArrayMap<>(8);
    private ArrayMap<View, Integer> mStickyCloseBottomCache = new ArrayMap<>(8);

    private ViewGroup mScrollView;
    private int mStickyPaddingTop = 0;
    private View mCurrentStickyView;
    private int mStickyViewMarginTop;

    private PinnedHeaderTouchHelper mPinnedHeaderTouchHelper;

    public StickyScrollDescendantView(Context context) {
        this(context, null);
    }

    public StickyScrollDescendantView(Context context, AttributeSet attrs) {
        this(context, attrs, View.NO_ID);
    }

    public StickyScrollDescendantView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this, false);
        getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof ScrollView) {
                mScrollView = (ViewGroup) parent;
                break;
            }
            parent = parent.getParent();
        }
    }

    public void setStickyPaddingTop(int stickyPaddingTop) {
        mStickyPaddingTop = stickyPaddingTop;
    }

    public void addStickyView(View stickyView, View closeView) {
        mStickyViewPairs.put(stickyView, closeView);
        requestLayout();
        invalidate();
    }

    public void removeStickyView(View stickyView) {
        mStickyViewPairs.remove(stickyView);

        requestLayout();
        invalidate();
    }

    public void reset() {
        mStickyViewPairs.clear();

        requestLayout();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        hideStickyView();

        mDescendantViewTopCache.clear();
        mStickyCloseBottomCache.clear();
        mSortedStickyViews.clear();
        Set<View> stickyViewSet = mStickyViewPairs.keySet();
        for (View stickyView : stickyViewSet) {
            if (stickyView.isShown()) {
                mSortedStickyViews.add(stickyView);
            }
        }
        Collections.sort(mSortedStickyViews, mStickyViewComparator);
    }

    private Comparator<View> mStickyViewComparator = new Comparator<View>() {
        @Override
        public int compare(View lhs, View rhs) {
            int lTop = getDescendantTop(lhs);
            int rTop = getDescendantTop(rhs);

            return lTop - rTop;
        }
    };

    /**
     * 获取后裔控件相对于scrollview的top
     * @param descendant
     * @return
     */
    private int getDescendantTop(View descendant) {
        if (descendant == null) {
            return -1;
        }
        if (!mDescendantViewTopCache.containsKey(descendant)) {
            int top = HotelViewUtils.getDescendantRelativeTop(mScrollView, descendant);
            mDescendantViewTopCache.put(descendant, top);
        }

        return mDescendantViewTopCache.get(descendant);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            scroll();
        }
    };

    public void scroll() {
        if (mScrollView == null) {
            return;
        }

        determineStickyView();
    }

    private void determineStickyView() {
        View stickyView = findStickyView();
        if (stickyView != null) {
            int stickyViewOffset = calculateStickyOffset(stickyView);
            if (stickyViewOffset < stickyView.getHeight()) {
                showStickView(stickyView, stickyViewOffset);
            } else {
                hideStickyView();
            }
        } else {
            hideStickyView();
        }
    }

    private View findStickyView() {
        for (int i = mSortedStickyViews.size() - 1; i >= 0; i--) {
            View view = mSortedStickyViews.get(i);
            int top = getDescendantTop(view) - mScrollView.getScrollY();
            if (mStickyPaddingTop > top) {
                return view;
            }

        }
        return null;
    }

    private int calculateStickyOffset(View stickyView) {
        int stickyViewBottom = getDescendantTop(stickyView) + stickyView.getHeight();
        int closeBottom = fetchStickyCloseBottom(stickyView);
        //若closeBottom在stickyView底部上面,则无需置顶
        if (closeBottom <= stickyViewBottom) {
            return Integer.MAX_VALUE;
        }

        int stickyVirtualViewBottom = mStickyPaddingTop + stickyView.getHeight();
        int closeBottomWithScrollY = closeBottom - mScrollView.getScrollY();
        if (stickyVirtualViewBottom > closeBottomWithScrollY) {
            return stickyVirtualViewBottom - closeBottomWithScrollY;
        }

        return 0;
    }

    private int fetchStickyCloseBottom(View stickyView) {
        if (mStickyCloseBottomCache.containsKey(stickyView)) {
            return mStickyCloseBottomCache.get(stickyView);
        }

        View closeView = mStickyViewPairs.get(stickyView);
        View nextStickyView = findNextStickyView(stickyView);

        int closeViewBottom = closeView == null ? Integer.MAX_VALUE : getDescendantTop(closeView) + closeView.getHeight();
        int nextPinnedHeaderViewTop = nextStickyView == null ? Integer.MAX_VALUE : getDescendantTop(nextStickyView);

        int closeBottom = Math.min(closeViewBottom, nextPinnedHeaderViewTop);
        mStickyCloseBottomCache.put(stickyView, closeBottom);

        return closeBottom;
    }

    private View findNextStickyView(View stickyView) {
        int index = mSortedStickyViews.indexOf(stickyView);
        if (index < mSortedStickyViews.size() - 1) {
            return mSortedStickyViews.get(index + 1);
        }
        return null;
    }

    private void hideStickyView() {
        if (mCurrentStickyView != null) {
            if (mCurrentStickyView.getVisibility() == INVISIBLE) {
                mCurrentStickyView.setVisibility(VISIBLE);
            }
            mCurrentStickyView = null;
            mStickyViewMarginTop = 0;
            invalidate();
        }
    }

    private void showStickView(View stickyView, int stickyViewOffset) {
        if (mCurrentStickyView != null && mCurrentStickyView != stickyView) {
            mCurrentStickyView.setVisibility(VISIBLE);
        }

        mCurrentStickyView = stickyView;
        mCurrentStickyView.setVisibility(INVISIBLE);
        mStickyViewMarginTop = mStickyPaddingTop + mScrollView.getScrollY() - stickyViewOffset
                - HotelViewUtils.getDescendantRelativeTop(mScrollView, this);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCurrentStickyView != null) {
            int saveCount = canvas.save();
            canvas.translate(0, mStickyViewMarginTop);
            canvas.clipRect(0, 0, getWidth(), mCurrentStickyView.getHeight());
            mCurrentStickyView.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mCurrentStickyView == null) {
            return dispatchTouchEventSafely(ev);
        }
        Rect rect = new Rect(0, mStickyViewMarginTop, mCurrentStickyView.getWidth(),
                mStickyViewMarginTop + mCurrentStickyView.getHeight());
        if (mPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mCurrentStickyView, rect, ev,
                0, -mStickyViewMarginTop)) {
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
