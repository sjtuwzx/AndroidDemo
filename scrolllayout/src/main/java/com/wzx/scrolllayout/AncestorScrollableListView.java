package com.wzx.scrolllayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by wangzhenxing on 17/2/3.
 */

public class AncestorScrollableListView extends ListView implements ScrollLinearLayout.DescendantTouchController {

    private boolean mShouldInterceptTouchEvent = false;

    public AncestorScrollableListView(Context context) {
        super(context);
    }

    public AncestorScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AncestorScrollableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void interceptTouchEvent(boolean intercept) {
        mShouldInterceptTouchEvent = intercept;
        setPressed(!mShouldInterceptTouchEvent);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mShouldInterceptTouchEvent || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean isPressed() {
        return !mShouldInterceptTouchEvent && super.isPressed();
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (mShouldInterceptTouchEvent) {
            return true;
        }
        return super.performItemClick(view, position, id);
    }
}
