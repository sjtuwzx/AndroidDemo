package com.wzx.sticky.scrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.wzx.sticky.PinnedHeaderTouchHelper;
import com.wzx.sticky.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ScrollView的descendant控件,实现自身的子控件吸顶效果
 * Created by wangzhenxing on 16/11/5.
 */
public class StickyScrollInsideView extends FrameLayout {

    private int mContentViewId;
    private ViewGroup mContentView;
    private List<Integer> mStickyChildIndexList = new ArrayList<>();
    private int mStickyPaddingTop = 0;

    private ScrollView mScrollView;
    private View mStickyView;
    private int mStickyViewMarginTop;
    private PinnedHeaderTouchHelper mPinnedHeaderTouchHelper;

    public StickyScrollInsideView(Context context) {
        this(context, null);
    }

    public StickyScrollInsideView(Context context, AttributeSet attrs) {
        this(context, attrs, View.NO_ID);
    }

    public StickyScrollInsideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickyScrollInsideView, defStyleAttr, 0);
        mContentViewId = a.getResourceId(R.styleable.StickyScrollInsideView_content_id, View.NO_ID);
        a.recycle();

        mPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this, false);
        getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = (ViewGroup) findViewById(mContentViewId);
    }

    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }

    public void setStickyPaddingTop(int stickyPaddingTop) {
        mStickyPaddingTop = stickyPaddingTop;
    }

    public void addStickyChildIndex(int childIndex) {
        mStickyChildIndexList.add(childIndex);
    }

    public void clearStickyViews() {
        mStickyChildIndexList.clear();
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            onScroll();
        }
    };

    public void onScroll() {
        if (mScrollView == null || mContentView == null) {
            return;
        }

        int stickyViewIndex = findStickyViewIndex();
        if (stickyViewIndex >= 0) {
            int stickyChildIndex = mStickyChildIndexList.get(stickyViewIndex);
            int nextStickyChildIndex = mStickyChildIndexList.get(stickyViewIndex + 1);

            int stickyCloseBottom = mContentView.getHeight();
            if (nextStickyChildIndex < mContentView.getChildCount()) {
                View nextStickyView = mContentView.getChildAt(nextStickyChildIndex);
                stickyCloseBottom = Math.min(stickyCloseBottom, nextStickyView.getTop());
            }
            int stickyCloseBottomRelativeScroll = HotelViewUtils.getDescendantRelativeTop(mScrollView, this) + stickyCloseBottom
                    - mScrollView.getScrollY() - mStickyPaddingTop;
            if (stickyCloseBottomRelativeScroll <= 0) {
                hideStickyView();
            } else {
                View stickyView = mContentView.getChildAt(stickyChildIndex);
                int stickyViewHeight = stickyView.getHeight();
                int marginTop = Math.min(0, stickyCloseBottomRelativeScroll - stickyViewHeight);
                showStickView(stickyView, marginTop);
            }
        } else {
            hideStickyView();
        }
    }

    private int findStickyViewIndex() {
        int size = mStickyChildIndexList.size();
        int childCount = mContentView.getChildCount();
        for (int i = size - 2; i >= 0; i--) {
            int childIndex = mStickyChildIndexList.get(i);
            if (childCount <= childIndex) {
                return -1;
            }
            View child = mContentView.getChildAt(childIndex);
            int childTopRelativeScroll = HotelViewUtils.getDescendantRelativeTop(mScrollView, child);
            if (childTopRelativeScroll < mScrollView.getScrollY() + mStickyPaddingTop) {
                return i;
            }
        }
        return -1;
    }

    private void hideStickyView() {
        mStickyView = null;
        mStickyViewMarginTop = 0;
        invalidate();
    }

    private void showStickView(View stickyView, int marginTop) {
        mStickyView = stickyView;
        mStickyViewMarginTop = mStickyPaddingTop + mScrollView.getScrollY() + marginTop
                - HotelViewUtils.getDescendantRelativeTop(mScrollView, this);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mStickyView != null) {
            int saveCount = canvas.save();
            canvas.translate(0, mStickyViewMarginTop);
            canvas.clipRect(0, 0, getWidth(), mStickyView.getHeight());
            mStickyView.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mStickyView == null) {
            return dispatchTouchEventSafely(ev);
        }
        Rect rect = new Rect(0, mStickyViewMarginTop, mStickyView.getWidth(),
                mStickyViewMarginTop + mStickyView.getHeight());
        if (mPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mStickyView, rect, ev,
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
