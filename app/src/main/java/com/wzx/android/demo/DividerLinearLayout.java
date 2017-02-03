package com.wzx.android.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2015/7/7.
 */
public class DividerLinearLayout extends LinearLayout {

    private List<View> mDividerChildren = new ArrayList<>();
    private Paint mPaint = new Paint();

    private int mDividerColor = Color.TRANSPARENT;
    private int mDividerSize;
    private int mDividerPaddingStart;
    private int mDividerPaddingEnd;

    public DividerLinearLayout(Context context) {
        this(context, null);
    }

    public DividerLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DividerLinearLayout, defStyleAttr, 0);

        mDividerColor = a.getColor(R.styleable.DividerLinearLayout_dividerColor, Color.TRANSPARENT);
        mDividerSize = a.getDimensionPixelSize(R.styleable.DividerLinearLayout_dividerSize, 0);
        mDividerPaddingStart = a.getDimensionPixelSize(R.styleable.DividerLinearLayout_dividerPaddingStart, 0);
        mDividerPaddingEnd = a.getDimensionPixelSize(R.styleable.DividerLinearLayout_dividerPaddingEnd, 0);

        a.recycle();
        mPaint.setColor(mDividerColor);
        mPaint.setStrokeWidth(mDividerSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mDividerChildren.clear();
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (!layoutParams.mIgnore && child.getVisibility() != GONE) {
                mDividerChildren.add(child);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int n = mDividerChildren.size();
        int orientation = getOrientation();
        for (int i = 0; i < n - 1; i++) {
            View child = mDividerChildren.get(i);
            if (orientation == HORIZONTAL) {
                int startY = getPaddingTop() + mDividerPaddingStart;
                int stopY = getHeight() - getPaddingBottom() - mDividerPaddingEnd;
                int x = child.getRight();
                canvas.drawLine(x, startY, x, stopY, mPaint);

            } else {
                int startX = getPaddingLeft() + mDividerPaddingStart;
                int stopX = getWidth() - getPaddingRight() - mDividerPaddingEnd;
                int y = child.getBottom();
                canvas.drawLine(startX, y, stopX, y, mPaint);
            }
        }
    }


    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        } else if (orientation == VERTICAL) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return null;
    }

    @Override
    protected LinearLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        private boolean mIgnore = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a =
                    c.obtainStyledAttributes(attrs, R.styleable.DividerLinearLayout_layout);

            mIgnore = a.getBoolean(R.styleable.DividerLinearLayout_layout_ignore, false);

            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }
}
