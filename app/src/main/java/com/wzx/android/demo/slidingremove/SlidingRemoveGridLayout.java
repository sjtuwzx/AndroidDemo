package com.wzx.android.demo.slidingremove;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Created by wang_zx on 2016/2/22.
 */
public class SlidingRemoveGridLayout extends RecycleGridLayout {

    public SlidingRemoveGridLayout(Context context) {
        this(context, null);
    }

    public SlidingRemoveGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingRemoveGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (closeSlidingRemoveChildren(ev)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean closeSlidingRemoveChildren(MotionEvent ev) {
        int x = (int)ev.getX();
        int y = (int)ev.getY();

        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View child = getChildAt(i);
            if (!isViewUnder(child, x, y) && child instanceof SlidingRemoveView) {
                SlidingRemoveView slidingRemoveView = (SlidingRemoveView)child;
                if (slidingRemoveView.isOpening()) {
                    slidingRemoveView.close();
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isViewUnder(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        return x >= view.getLeft() &&
                x < view.getRight() &&
                y >= view.getTop() &&
                y < view.getBottom();
    }
}
