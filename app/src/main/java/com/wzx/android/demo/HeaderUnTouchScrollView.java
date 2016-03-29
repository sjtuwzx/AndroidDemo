package com.wzx.android.demo;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by wang_zx on 2015/8/17.
 */
public class HeaderUnTouchScrollView extends ScrollView {

    public HeaderUnTouchScrollView(Context context) {
        this(context, null);
    }

    public HeaderUnTouchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public HeaderUnTouchScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int paddingTop = getPaddingTop();
        int scrollY = getScrollY();
        int headUnTouchHeight = Math.max(0, Math.min(paddingTop, paddingTop - scrollY));

        int action = ev.getAction();
        if ((action & MotionEventCompat.ACTION_MASK) == MotionEvent.ACTION_DOWN
                && ev.getY() < headUnTouchHeight) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

}
