package com.wzx.android.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by wangzhenxing on 17/1/19.
 */

public class ObservableScrollView extends ScrollView {

    public ObservableScrollView(Context context) {
        this(context, null);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, View.NO_ID);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChanged(this, getScrollX(), getScrollY());
        }
    }

    private OnScrollChangeListener mOnScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        mOnScrollChangeListener = listener;
    }

    public interface OnScrollChangeListener {

        void onScrollChanged(View v, int scrollX, int scrollY);
    }

}
