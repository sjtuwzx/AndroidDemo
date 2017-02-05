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
 * Created by wangzhenxing on 16/8/4.
 */
public class HotelVerticalLabelView extends View {

    private List<HotelLabelBaseDrawable> mLabelDrawables = new ArrayList<HotelLabelBaseDrawable>();
    private List<HotelLabelBaseDrawable> mVisibleLabelDrawables = new ArrayList<HotelLabelBaseDrawable>();

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void recycle(HotelCommonRecycleBin recycleBin) {
        recycle(mLabelDrawables, recycleBin);

        mVisibleLabelDrawables.clear();
        requestLayout();
        invalidate();
    }

    private static void recycle(List<HotelLabelBaseDrawable> drawables, HotelCommonRecycleBin recycleBin) {
        for (HotelLabelBaseDrawable drawable : drawables) {
            drawable.setCallback(null);
            recycleBin.addScrapObject(drawable);
        }
        drawables.clear();
    }

    public void refreshLabelDrawables(List<HotelLabelBaseDrawable> labelDrawables) {
        mLabelDrawables.clear();
        mLabelDrawables.addAll(labelDrawables);
        for (HotelLabelBaseDrawable drawable : mLabelDrawables) {
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
        int height = Integer.MAX_VALUE;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.UNSPECIFIED) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }

        mVisibleLabelDrawables.clear();
        measureChildDrawables(mLabelDrawables, mVisibleLabelDrawables, height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(measureWidth(), MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(measureHeight(), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureChildDrawables(List<HotelLabelBaseDrawable> sourceDrawables, List<HotelLabelBaseDrawable> visibleDrawables, int remainHeight) {
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
        for (HotelLabelBaseDrawable drawable : mVisibleLabelDrawables) {
            width = Math.max(width, drawable.getMeasuredWidth());
        }
        return width;
    }

    private int measureHeight() {
        int height = 0;
        for (HotelLabelBaseDrawable drawable : mVisibleLabelDrawables) {
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
        for (HotelLabelBaseDrawable labelDrawable : mVisibleLabelDrawables) {
            int measuredWidth = labelDrawable.getMeasuredWidth();
            int measuredHeight = labelDrawable.getMeasuredHeight();

            labelDrawable.layout(width - measuredWidth, offsetY, width, offsetY + measuredHeight);

            offsetY += measuredHeight + mVerticalSpacing;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (HotelLabelBaseDrawable labelDrawable : mVisibleLabelDrawables) {
            labelDrawable.draw(canvas);
        }
    }
}
