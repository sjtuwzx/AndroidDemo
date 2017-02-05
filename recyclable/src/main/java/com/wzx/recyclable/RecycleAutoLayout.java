package com.wzx.recyclable;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhenxing on 16/9/22.
 */
public class RecycleAutoLayout extends RecycleBaseLayout {

    private List<ViewLine> mViewLines = new ArrayList<ViewLine>();

    private int mMaxLine;
    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    public RecycleAutoLayout(Context context) {
        this(context, null);
    }

    public RecycleAutoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecycleAutoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecycleAutoLayout, defStyleAttr, 0);
        mMaxLine = a.getInt(R.styleable.RecycleAutoLayout_max_line, Integer.MAX_VALUE);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.RecycleAutoLayout_horizontal_spacing, 0);
        mVerticalSpacing = a.getDimensionPixelSize(R.styleable.RecycleAutoLayout_vertical_spacing, 0);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewLines.clear();

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int remainWidth = width - hPadding;
        ViewLine currentViewLine = null;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureView(child, widthMeasureSpec, heightMeasureSpec, hPadding, vPadding);

            if (currentViewLine == null || remainWidth < child.getMeasuredWidth()) {
                if (mViewLines.size() >= mMaxLine) {
                    break;
                }
                currentViewLine = new ViewLine();
                mViewLines.add(currentViewLine);
                remainWidth = width - hPadding;
            }

            currentViewLine.mHeight = Math.max(currentViewLine.mHeight, child.getMeasuredHeight());
            currentViewLine.mChildren.add(child);

            remainWidth -= child.getMeasuredWidth() + mHorizontalSpacing;
        }

        int height = 0;
        for (ViewLine viewLine : mViewLines) {
            height += viewLine.mHeight;
        }
        height += vPadding + mVerticalSpacing * Math.max(0, mViewLines.size() - 1);
        setMeasuredDimension(width, height);
    }

    private static void measureView(View view, int widthMeasureSpec, int heightMeasureSpec, int hPadding, int vPadding) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, hPadding, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, vPadding, lp.height);

        view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = getPaddingTop();
        for (ViewLine viewLine : mViewLines) {
            int left = getPaddingLeft();
            for (View child : viewLine.mChildren) {
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());

                left += child.getMeasuredWidth() + mHorizontalSpacing;
            }
            top += viewLine.mHeight + mVerticalSpacing;
        }
    }

    private static final class ViewLine {
        int mHeight = 0;
        List<View> mChildren = new ArrayList<View>();
    }
}
