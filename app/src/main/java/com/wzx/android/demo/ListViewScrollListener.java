package com.wzx.android.demo;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by wangzhenxing on 17/1/31.
 */

public class ListViewScrollListener implements AbsListView.OnScrollListener, View.OnTouchListener {

    private int mFirstVisiblePosition = -1;
    private int mFirstVisibleViewHeight = 0;
    private int mFirstVisibleViewTop = 0;
    private boolean mHasTouch = false;

    private OnScrollListener mOnScrollListener;

    public ListViewScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (scrollState == SCROLL_STATE_IDLE) {
            mFirstVisiblePosition = -1;
            mFirstVisibleViewHeight = 0;
            mFirstVisibleViewTop = 0;
            mHasTouch = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (view.getChildCount() == 0) {
            return;
        }
        View firstView = view.getChildAt(0);
        if (mFirstVisiblePosition < 0) {
            mFirstVisiblePosition = firstVisibleItem;
            mFirstVisibleViewHeight = firstView.getHeight();
            mFirstVisibleViewTop = firstView.getTop();
        } else {
            int firstViewPosition = firstVisibleItem;
            int firstViewHeight = firstView.getHeight();
            int firstViewTop = firstView.getTop();

            int dy = 0;
            if (firstViewPosition == mFirstVisiblePosition) {
                dy = mFirstVisibleViewTop - firstViewTop;
            } else if (firstViewPosition - mFirstVisiblePosition == 1) {
                dy = mFirstVisibleViewHeight + mFirstVisibleViewTop - firstViewTop;
            } else if (mFirstVisiblePosition - firstViewPosition == 1){
                dy = - (firstViewHeight + firstViewTop - mFirstVisibleViewTop);
            }

            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, dy, mHasTouch);
            }

            mFirstVisiblePosition = firstViewPosition;
            mFirstVisibleViewHeight = firstViewHeight;
            mFirstVisibleViewTop = firstViewTop;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public interface OnScrollListener extends AbsListView.OnScrollListener {
        void onScroll(AbsListView view, int dy, boolean fromUser);
    }
}
