package com.wzx.android.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by wang_zx on 2015/12/28.
 */
public class ChildrenLayoutObservableListView extends ListView {

    public ChildrenLayoutObservableListView(Context context) {
        super(context);
    }

    public ChildrenLayoutObservableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildrenLayoutObservableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        if (mOnChildrenLayoutListener != null) {
            mOnChildrenLayoutListener.onChildrenLayout(this);
        }
    }

    private OnChildrenLayoutListener mOnChildrenLayoutListener;

    public void setOnChildrenLayoutListener(OnChildrenLayoutListener listener) {
        mOnChildrenLayoutListener = listener;
    }

    public interface OnChildrenLayoutListener {
        void onChildrenLayout(ListView listView);
    }
}
