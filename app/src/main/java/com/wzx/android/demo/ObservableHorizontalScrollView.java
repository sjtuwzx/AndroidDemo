package com.wzx.android.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by wang_zx on 2016/1/27.
 */
public class ObservableHorizontalScrollView extends HorizontalScrollView {

    public ObservableHorizontalScrollView(Context context) {
        super(context);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
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
