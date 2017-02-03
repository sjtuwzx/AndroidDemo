package com.wzx.android.demo.recycle.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangzhenxing on 16/7/18.
 */
public class RecycleTestView extends ViewGroup {

    public RecycleTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleTestView(Context context) {
        super(context);
    }

    public RecycleTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    public void addView(View view) {
        addViewInLayout(view, 0, view.getLayoutParams());

        requestLayout();
        invalidate();
    }

    public void removeChildren() {
        removeAllViewsInLayout();

        requestLayout();
        invalidate();
    }
}
