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
 * Created by wang_zx on 2015/7/27.
 */
public class HotelFilterLayout extends ViewGroup {

    private List<ViewLine> mViewLines = new ArrayList<ViewLine>();

    private int mMaxLine = Integer.MAX_VALUE;

    private int mHorizontalSpacing = 0;
    private int mVerticalSpacing = 0;

    private int mPaddingLastLineEnd = 0;

    private View mPreferenceView;
    private boolean mAutoStretchChildren = false;

    public HotelFilterLayout(Context context) {
        this(context, null);
    }

    public HotelFilterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelFilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HotelFilterLayout, defStyleAttr, 0);
        mMaxLine = a.getInt(R.styleable.HotelFilterLayout_max_line, Integer.MAX_VALUE);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.HotelFilterLayout_horizontal_spacing, 0);
        mVerticalSpacing = a.getDimensionPixelSize(R.styleable.HotelFilterLayout_vertical_spacing, 0);
        mPaddingLastLineEnd = a.getDimensionPixelSize(R.styleable.HotelFilterLayout_paddingLastLineEnd, 0);
        mAutoStretchChildren = a.getBoolean(R.styleable.HotelFilterLayout_autoStretchChildren, false);
        a.recycle();
    }

    public void setPaddingLastLineEnd(int paddingLastLineEnd) {
        mPaddingLastLineEnd = paddingLastLineEnd;
        requestLayout();
        invalidate();
    }

    public void setPreferenceView(View view) {
        mPreferenceView = view;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewLines.clear();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = getPaddingTop();

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int remainWidth = width - hPadding;
        ViewLine currentViewLine = null;
        boolean needAppendPreferenceView = containChild(mPreferenceView);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureView(child, widthMeasureSpec, heightMeasureSpec, hPadding, vPadding);

            if (i == 0 || remainWidth < child.getMeasuredWidth()) {
                if (mViewLines.size() >= mMaxLine) {
                    if (needAppendPreferenceView) {
                        currentViewLine.height = Math.max(currentViewLine.height, mPreferenceView.getMeasuredHeight());
                        currentViewLine.children.add(mPreferenceView);
                    }
                    break;
                }
                height += currentViewLine != null ? currentViewLine.height + mVerticalSpacing : 0;

                currentViewLine = new ViewLine();
                mViewLines.add(currentViewLine);

                remainWidth = width - hPadding;
                if (mViewLines.size() == mMaxLine) {
                    remainWidth -= mPaddingLastLineEnd;
                    if (needAppendPreferenceView) {
                        measureView(mPreferenceView, widthMeasureSpec, heightMeasureSpec, hPadding, vPadding);
                        remainWidth -= mPreferenceView.getMeasuredWidth() + mHorizontalSpacing;
                    }
                }
            }

            currentViewLine.top = height;
            currentViewLine.height = Math.max(currentViewLine.height, child.getMeasuredHeight());
            currentViewLine.children.add(child);
            if (child == mPreferenceView) {
                if (mViewLines.size() == mMaxLine) {
                    remainWidth += child.getMeasuredWidth() + mHorizontalSpacing;
                }
                needAppendPreferenceView = false;
            }
            remainWidth -= child.getMeasuredWidth() + mHorizontalSpacing;
            currentViewLine.remainWidth = remainWidth;
        }
        height += currentViewLine != null ? currentViewLine.height : 0;
        height += getPaddingBottom();
        setMeasuredDimension(width, height);

        if (mAutoStretchChildren) {
            for (ViewLine viewLine : mViewLines) {
                int lineChildrenCount = viewLine.children.size();
                int stretchWidth = (viewLine.remainWidth + mHorizontalSpacing) / lineChildrenCount;
                for (View child : viewLine.children) {
                    child.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth() + stretchWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY));
                }
            }
        }
    }

    private static void measureView(View view, int widthMeasureSpec, int heightMeasureSpec, int hPadding, int vPadding) {
        LayoutParams lp = view.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, hPadding, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, vPadding, lp.height);

        view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private boolean containChild(View view) {
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View child = getChildAt(i);
            if (child == view) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (ViewLine viewLine : mViewLines) {
            int left = getPaddingLeft();
            for (View child : viewLine.children) {
                child.layout(left, viewLine.top, left + child.getMeasuredWidth(), viewLine.top + child.getMeasuredHeight());

                left += child.getMeasuredWidth() + mHorizontalSpacing;
            }
        }
    }

    private static class ViewLine {
        int top = 0;
        int height = 0;
        List<View> children = new ArrayList<View>();
        int remainWidth = 0;
    }
}