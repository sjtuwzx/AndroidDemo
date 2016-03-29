package com.wzx.android.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/5/19.
 */
public class DragScrollView extends ScrollView {

    private static final String TAG = DragScrollView.class.getSimpleName();

    private DragContent mDragContent;

    public DragScrollView(Context context) {
        this(context, null);
    }

    public DragScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragContent = (DragContent)findViewById(R.id.drag_content);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int scrollY = getScrollY();
        if (mDragContent != null && mDragContent.onParentTouchEvent(scrollY, ev) ) {
            return true;
        }
        return super.onTouchEvent(ev);
    }
}
