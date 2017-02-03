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
 * Created by wangzhenxing on 16/8/4.
 */
public class HotelVerticalLabelView extends View {

    private List<HotelLabelDrawable> mLabelDrawables = new ArrayList<HotelLabelDrawable>();
    private List<HotelLabelDrawable> mVisibleLabelDrawables = new ArrayList<HotelLabelDrawable>();

    private int mChildWidth;
    private int mChildHeight;
    private int mVerticalSpacing;

    public HotelVerticalLabelView(Context context) {
        this(context, null);
    }

    public HotelVerticalLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelVerticalLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HotelVerticalLabelView, defStyleAttr, 0);
        mChildWidth = a.getDimensionPixelSize(R.styleable.HotelVerticalLabelView_child_width, 0);
        mChildHeight = a.getDimensionPixelSize(R.styleable.HotelVerticalLabelView_child_height, 0);
        mVerticalSpacing = a.getDimensionPixelSize(R.styleable.HotelVerticalLabelView_vertical_spacing, 0);
        a.recycle();
    }

    public void recycle(HotelCommonRecycleBin recycleBin) {
        recycle(mLabelDrawables, recycleBin);

        mVisibleLabelDrawables.clear();
    }

    private static void recycle(List<HotelLabelDrawable> drawables, HotelCommonRecycleBin recycleBin) {
        for (HotelLabelDrawable drawable : drawables) {
            drawable.setCallback(null);
            recycleBin.addScrapObject(drawable);
        }
        drawables.clear();
    }

    public void refreshLabelDrawables(List<HotelLabelDrawable> labelDrawables) {
        mLabelDrawables.clear();
        mLabelDrawables.addAll(labelDrawables);
        for (HotelLabelDrawable drawable : mLabelDrawables) {
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
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mVisibleLabelDrawables.clear();
        measureChildDrawables(mLabelDrawables, mVisibleLabelDrawables, height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(measureWidth(), MeasureSpec.EXACTLY);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(measureHeight(), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureChildDrawables(List<HotelLabelDrawable> sourceDrawables, List<HotelLabelDrawable> visibleDrawables, int remainHeight) {
        for (HotelLabelDrawable drawable : sourceDrawables) {
            int widthMeasureSpec = MeasureSpec.UNSPECIFIED;
            int heightMeasureSpec = MeasureSpec.UNSPECIFIED;
            if (mChildWidth > 0) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY);
            }
            if (mChildHeight > 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY);
            }
            drawable.measure(widthMeasureSpec, heightMeasureSpec);
            int drawableHeight = drawable.getMeasuredHeight();
            if (remainHeight > drawableHeight) {
                visibleDrawables.add(drawable);
                remainHeight -= drawableHeight + mVerticalSpacing;
            } else {
                break;
            }
        }
    }

    private int measureWidth() {
        int width = 0;
        for (HotelLabelDrawable drawable : mVisibleLabelDrawables) {
            width = Math.max(width, drawable.getMeasuredWidth());
        }
        return width;
    }

    private int measureHeight() {
        int height = 0;
        for (HotelLabelDrawable drawable : mVisibleLabelDrawables) {
            height += drawable.getMeasuredHeight();
        }
        if (!mVisibleLabelDrawables.isEmpty()) {
            height += mVerticalSpacing * (mVisibleLabelDrawables.size() - 1);
        }

        return height;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        layoutDrawables();

    }

    private void layoutDrawables() {
        int offsetY = 0;
        int width = getWidth();
        for (HotelLabelDrawable labelDrawable : mVisibleLabelDrawables) {
            int measuredWidth = labelDrawable.getMeasuredWidth();
            int measuredHeight = labelDrawable.getMeasuredHeight();

            labelDrawable.layout(width - measuredWidth, offsetY, width, offsetY + measuredHeight);

            offsetY += measuredHeight + mVerticalSpacing;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (HotelLabelDrawable labelDrawable : mVisibleLabelDrawables) {
            labelDrawable.draw(canvas);
        }
    }
}
