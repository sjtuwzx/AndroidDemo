package com.wzx.scrolllayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by wangzhenxing on 17/2/3.
 */

public class AncestorScrollableLayout extends LinearLayout implements ScrollLinearLayout.DescendantTouchController {

    private boolean mShouldInterceptTouchEvent = false;

    public AncestorScrollableLayout(Context context) {
        super(context);
    }

    public AncestorScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AncestorScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void interceptTouchEvent(boolean intercept) {
        mShouldInterceptTouchEvent = intercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mShouldInterceptTouchEvent || super.onInterceptTouchEvent(ev);
    }
}
