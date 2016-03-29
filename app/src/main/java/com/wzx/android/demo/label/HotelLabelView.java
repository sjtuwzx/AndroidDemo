package com.wzx.android.demo.label;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2015/12/17.
 */
public class HotelLabelView extends View {

    private List<HotelLabelDrawable> mLeftLabelDrawables = new ArrayList<HotelLabelDrawable>();
    private List<HotelLabelDrawable> mRightLabelDrawables = new ArrayList<HotelLabelDrawable>();
    private List<HotelLabelDrawable> mPriorityDisplayRightDrawables = new ArrayList<HotelLabelDrawable>();

    private List<HotelLabelDrawable> mVisibleLeftLabelDrawables = new ArrayList<HotelLabelDrawable>();
    private List<HotelLabelDrawable> mVisibleRightLabelDrawables = new ArrayList<HotelLabelDrawable>();

    private int mHorizontalSpacing;

    public HotelLabelView(Context context) {
        this(context, null);
    }

    public HotelLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HotelLabelView, defStyleAttr, 0);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.HotelLabelView_horizontal_spacing, 0);
        a.recycle();
    }

    public void refreshLeftLabelDrawables(List<HotelLabelDrawable> leftLabelDrawables) {
        mLeftLabelDrawables = leftLabelDrawables;
        for (HotelLabelDrawable drawable : mLeftLabelDrawables) {
            drawable.setCallback(this);
        }
        requestLayout();
        invalidate();
    }

    public void refreshRightLabelDrawables(List<HotelLabelDrawable> rightLabelDrawables) {
        mRightLabelDrawables = rightLabelDrawables;
        for (HotelLabelDrawable drawable : mRightLabelDrawables) {
            drawable.setCallback(this);
        }
        requestLayout();
        invalidate();
    }

    public void refreshPriorityDisplayRightDrawables(List<HotelLabelDrawable> priorityDisplayRightDrawables) {
        mPriorityDisplayRightDrawables = priorityDisplayRightDrawables;
        for (HotelLabelDrawable drawable : mPriorityDisplayRightDrawables) {
            drawable.setCallback(this);
        }
        requestLayout();
        invalidate();
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);

        mVisibleLeftLabelDrawables.clear();
        mVisibleRightLabelDrawables.clear();
        int remainWidth = measureChildDrawables(mPriorityDisplayRightDrawables, mVisibleRightLabelDrawables, width);
        remainWidth = measureChildDrawables(mLeftLabelDrawables, mVisibleLeftLabelDrawables, remainWidth);
        measureChildDrawables(mRightLabelDrawables, mVisibleRightLabelDrawables, remainWidth);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int height = measureHeight();
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    private int measureChildDrawables(List<HotelLabelDrawable> sourceDrawables, List<HotelLabelDrawable> visibleDrawables, int remainWidth) {
        for (HotelLabelDrawable drawable : sourceDrawables) {
            drawable.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int drawableWidth = drawable.getMeasuredWidth();
            if (remainWidth > drawableWidth) {
                visibleDrawables.add(drawable);
                remainWidth -= drawableWidth + mHorizontalSpacing;
            } else {
                break;
            }
        }

        return remainWidth;
    }

    private int measureHeight() {
        int height = 0;
        for (HotelLabelDrawable drawable : mVisibleLeftLabelDrawables) {
            height = Math.max(height, drawable.getMeasuredHeight());
        }
        for (HotelLabelDrawable drawable : mVisibleRightLabelDrawables) {
            height = Math.max(height, drawable.getMeasuredHeight());
        }
        return height;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        layoutLeftDrawables();
        layoutRightDrawables();

    }

    private void layoutLeftDrawables() {
        int offsetX = 0;
        int height = getHeight();
        for (HotelLabelDrawable labelDrawable : mVisibleLeftLabelDrawables) {
            int measuredWidth = labelDrawable.getMeasuredWidth();
            int measuredHeight = labelDrawable.getMeasuredHeight();

            labelDrawable.setBounds(offsetX, height - measuredHeight, offsetX + measuredWidth, height);

            offsetX += measuredWidth + mHorizontalSpacing;
        }
    }

    private void layoutRightDrawables() {
        int offsetX = 0;
        int width = getWidth();
        int height = getHeight();
        for (HotelLabelDrawable labelDrawable : mVisibleRightLabelDrawables) {
            int measuredWidth = labelDrawable.getMeasuredWidth();
            int measuredHeight = labelDrawable.getMeasuredHeight();

            labelDrawable.setBounds(width - offsetX - measuredWidth, height - measuredHeight, width - offsetX, height);

            offsetX += measuredWidth + mHorizontalSpacing;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (HotelLabelDrawable labelDrawable : mVisibleLeftLabelDrawables) {
            labelDrawable.draw(canvas);
        }

        for (HotelLabelDrawable labelDrawable : mVisibleRightLabelDrawables) {
            labelDrawable.draw(canvas);
        }
    }
}
