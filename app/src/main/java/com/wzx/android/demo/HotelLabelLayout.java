package com.wzx.android.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhenxing on 16/4/18.
 */
public class HotelLabelLayout extends ViewGroup {

    private List<View> mVisibleLabels = new ArrayList<View>();

    private int mHorizontalSpacing;

    public HotelLabelLayout(Context context) {
        this(context, null);
    }

    public HotelLabelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelLabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HotelLabelLayout, defStyleAttr, 0);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.HotelLabelLayout_horizontal_spacing, 0);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mVisibleLabels.clear();

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int remainWidth = width - hPadding;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureView(child, widthMeasureSpec, heightMeasureSpec, hPadding, vPadding);

            if (remainWidth >= child.getMeasuredWidth()) {
                mVisibleLabels.add(child);
                remainWidth -= child.getMeasuredWidth() + mHorizontalSpacing;
                height = Math.max(height, child.getMeasuredHeight());
            }
        }

        height += vPadding;
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private static void measureView(View view, int widthMeasureSpec, int heightMeasureSpec, int hPadding, int vPadding) {
        LayoutParams lp = view.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, hPadding, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, vPadding, lp.height);

        view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();

        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View child = getChildAt(i);
            if (mVisibleLabels.contains(child)) {
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());

                left += child.getMeasuredWidth() + mHorizontalSpacing;
            } else {
                child.layout(0, 0, 0, 0);
            }
        }
    }
}
