package com.wzx.label;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2015/12/17.
 */
public class HotelLabelView extends View {

    private List<HotelLabelBaseDrawable> mLeftLabelDrawables = new ArrayList<HotelLabelBaseDrawable>();
    private List<HotelLabelBaseDrawable> mRightLabelDrawables = new ArrayList<HotelLabelBaseDrawable>();
    private List<HotelLabelBaseDrawable> mPriorityDisplayRightDrawables = new ArrayList<HotelLabelBaseDrawable>();

    private List<HotelLabelBaseDrawable> mVisibleLeftLabelDrawables = new ArrayList<HotelLabelBaseDrawable>();
    private List<HotelLabelBaseDrawable> mVisibleRightLabelDrawables = new ArrayList<HotelLabelBaseDrawable>();

    private int mChildWidth;
    private int mChildHeight;
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
        mChildWidth = a.getDimensionPixelSize(R.styleable.HotelLabelView_child_width, 0);
        mChildHeight = a.getDimensionPixelSize(R.styleable.HotelLabelView_child_height, 0);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.HotelLabelView_horizontal_spacing, 0);
        a.recycle();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void recycle(HotelCommonRecycleBin recycleBin) {
        recycle(mLeftLabelDrawables, recycleBin);
        recycle(mRightLabelDrawables, recycleBin);
        recycle(mPriorityDisplayRightDrawables, recycleBin);

        mVisibleLeftLabelDrawables.clear();
        mVisibleRightLabelDrawables.clear();
        requestLayout();
        invalidate();
    }

    public void setChildWidth(int childWidth) {
        mChildWidth = childWidth;
        requestLayout();
        invalidate();
    }

    public void setChildHeight(int childHeight) {
        mChildHeight = childHeight;
        requestLayout();
        invalidate();
    }

    public void setHorizontalSpacing(int mHorizontalSpacing) {
        this.mHorizontalSpacing = mHorizontalSpacing;
    }

    private static void recycle(List<HotelLabelBaseDrawable> drawables, HotelCommonRecycleBin recycleBin) {
        for (HotelLabelBaseDrawable drawable : drawables) {
            drawable.setCallback(null);
            recycleBin.addScrapObject(drawable);
        }
        drawables.clear();
    }

    private static void clearLabelDrawables(List<HotelLabelBaseDrawable> labelDrawables) {
        for (HotelLabelBaseDrawable drawable : labelDrawables) {
            drawable.setCallback(null);
        }
        labelDrawables.clear();
    }

    public void setSingleLabel(HotelLabelBaseDrawable labelDrawable) {
        clearLabelDrawables(mLeftLabelDrawables);
        mLeftLabelDrawables.add(labelDrawable);
        labelDrawable.setCallback(this);

        requestLayout();
        invalidate();
    }

    public void refreshLeftLabelDrawables(List<HotelLabelBaseDrawable> leftLabelDrawables) {
        clearLabelDrawables(mLeftLabelDrawables);
        mLeftLabelDrawables.addAll(leftLabelDrawables);
        for (HotelLabelBaseDrawable drawable : mLeftLabelDrawables) {
            drawable.setCallback(this);
        }
        requestLayout();
        invalidate();
    }

    public void refreshRightLabelDrawables(List<HotelLabelBaseDrawable> rightLabelDrawables) {
        clearLabelDrawables(mRightLabelDrawables);
        mRightLabelDrawables.addAll(rightLabelDrawables);
        for (HotelLabelBaseDrawable drawable : mRightLabelDrawables) {
            drawable.setCallback(this);
        }
        requestLayout();
        invalidate();
    }

    public void refreshPriorityDisplayRightDrawables(List<HotelLabelBaseDrawable> priorityDisplayRightDrawables) {
        clearLabelDrawables(mPriorityDisplayRightDrawables);
        mPriorityDisplayRightDrawables.addAll(priorityDisplayRightDrawables);
        for (HotelLabelBaseDrawable drawable : mPriorityDisplayRightDrawables) {
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
        int width = Integer.MAX_VALUE;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.UNSPECIFIED) {
            int hPadding = getPaddingLeft() + getPaddingRight();
            width = MeasureSpec.getSize(widthMeasureSpec) - hPadding;
        }

        mVisibleLeftLabelDrawables.clear();
        mVisibleRightLabelDrawables.clear();
        int remainWidth = measureChildDrawables(mPriorityDisplayRightDrawables, mVisibleRightLabelDrawables, width);
        remainWidth = measureChildDrawables(mLeftLabelDrawables, mVisibleLeftLabelDrawables, remainWidth);
        measureChildDrawables(mRightLabelDrawables, mVisibleRightLabelDrawables, remainWidth);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(measureWidth(), MeasureSpec.EXACTLY);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(measureHeight(), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureChildDrawables(List<HotelLabelBaseDrawable> sourceDrawables, List<HotelLabelBaseDrawable> visibleDrawables, int remainWidth) {
        for (HotelLabelBaseDrawable drawable : sourceDrawables) {
            int widthMeasureSpec = MeasureSpec.UNSPECIFIED;
            int heightMeasureSpec = MeasureSpec.UNSPECIFIED;
            if (mChildWidth > 0) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY);
            }
            if (mChildHeight > 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY);
            }
            drawable.measure(widthMeasureSpec, heightMeasureSpec);
            int drawableWidth = drawable.getMeasuredWidth();
            if (remainWidth >= drawableWidth) {
                visibleDrawables.add(drawable);
                remainWidth -= drawableWidth + mHorizontalSpacing;
            } else {
                break;
            }
        }

        return remainWidth;
    }

    private int measureWidth() {
        int width = getPaddingLeft() + getPaddingRight();
        for (HotelLabelBaseDrawable drawable : mVisibleLeftLabelDrawables) {
            width += drawable.getMeasuredWidth();
        }
        for (HotelLabelBaseDrawable drawable : mVisibleRightLabelDrawables) {
            width += drawable.getMeasuredWidth();
        }
        int visibleLabelCount = mVisibleLeftLabelDrawables.size() + mVisibleRightLabelDrawables.size();
        if (visibleLabelCount > 1) {
            width += mHorizontalSpacing * (visibleLabelCount - 1);
        }
        return width;
    }

    private int measureHeight() {
        int height = 0;
        for (HotelLabelBaseDrawable drawable : mVisibleLeftLabelDrawables) {
            height = Math.max(height, drawable.getMeasuredHeight());
        }
        for (HotelLabelBaseDrawable drawable : mVisibleRightLabelDrawables) {
            height = Math.max(height, drawable.getMeasuredHeight());
        }
        height += getPaddingTop() + getPaddingBottom();
        return height;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        layoutLeftDrawables();
        layoutRightDrawables();

    }

    private void layoutLeftDrawables() {
        int left = getPaddingLeft();
        int bottom = getHeight() - getPaddingBottom();
        for (HotelLabelBaseDrawable labelDrawable : mVisibleLeftLabelDrawables) {
            int measuredWidth = labelDrawable.getMeasuredWidth();
            int measuredHeight = labelDrawable.getMeasuredHeight();

            labelDrawable.layout(left, bottom - measuredHeight, left + measuredWidth, bottom);

            left += measuredWidth + mHorizontalSpacing;
        }
    }

    private void layoutRightDrawables() {
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom();
        for (HotelLabelBaseDrawable labelDrawable : mVisibleRightLabelDrawables) {
            int measuredWidth = labelDrawable.getMeasuredWidth();
            int measuredHeight = labelDrawable.getMeasuredHeight();

            labelDrawable.layout(right - measuredWidth, bottom - measuredHeight, right, bottom);

            right -= measuredWidth + mHorizontalSpacing;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (HotelLabelBaseDrawable labelDrawable : mVisibleLeftLabelDrawables) {
            labelDrawable.draw(canvas);
        }

        for (HotelLabelBaseDrawable labelDrawable : mVisibleRightLabelDrawables) {
            labelDrawable.draw(canvas);
        }
    }
}
