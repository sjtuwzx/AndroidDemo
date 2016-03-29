package com.wzx.android.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2015/7/7.
 */
public class DividerLinearLayout extends LinearLayout implements View.OnClickListener{

    private List<View> mVisibleChildren = new ArrayList<>();
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
        mVisibleChildren.clear();
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                mVisibleChildren.add(child);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int N = mVisibleChildren.size();
        int orientation = getOrientation();
        for (int i = 0; i < N - 1; i++) {
            View child = mVisibleChildren.get(i);
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
    public void onClick(View v) {
        v.setVisibility(View.GONE);
    }
}
