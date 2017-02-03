package com.wzx.android.demo.recycleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.wzx.android.demo.v2.R;

import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 点评列表图片展示控件，实现全局child view复用
 *
 * @author wang_zx
 */
public class RecycleGridLayout extends RecycleBaseLayout {

    private static final String TAG = RecycleGridLayout.class.getSimpleName();

    private int mColumnCount = 1;
    private int mChildWidth = 0;
    private int mChildHeight = 0;
    private int mHorizontalSpacing = 0;
    private int mVerticalSpacing = 0;

    private Paint mPaint = new Paint();
    private int mDividerColor;
    private int mDividerSize;
    private boolean mDrawEdge = false;

    public RecycleGridLayout(Context context) {
        this(context, null);
    }

    public RecycleGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecycleGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecycleGridLayout, defStyle, 0);
        mColumnCount = a.getInt(R.styleable.RecycleGridLayout_column_count, 1);
        mChildWidth = a.getDimensionPixelSize(R.styleable.RecycleGridLayout_child_width, 0);
        mChildHeight = a.getDimensionPixelSize(R.styleable.RecycleGridLayout_child_height, 0);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.RecycleGridLayout_horizontal_spacing, 0);
        mVerticalSpacing = a.getDimensionPixelSize(R.styleable.RecycleGridLayout_vertical_spacing, 0);

        mDividerColor = a.getColor(R.styleable.RecycleGridLayout_dividerColor, Color.TRANSPARENT);
        mDividerSize = a.getDimensionPixelSize(R.styleable.RecycleGridLayout_dividerSize, 0);
        mDrawEdge = a.getBoolean(R.styleable.RecycleGridLayout_drawEdge, false);

        a.recycle();

        mPaint.setColor(mDividerColor);
        mPaint.setStrokeWidth(mDividerSize);
    }

    public void setChildSize(int width, int height) {
        mChildWidth = width;
        mChildHeight = height;
        requestLayout();
    }

    public void setColumnCount(int count) {
        mColumnCount = count;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int height = vPadding;

        int columnHeight = 0;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            if (mColumnCount > 0 && i != 0 && i % mColumnCount == 0) {
                height += columnHeight + mVerticalSpacing;
                columnHeight = 0;
            }
            View child = getChildAt(i);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, hPadding, lp.width);
            if (mChildWidth > 0) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY);
            }
            int childHorizontalSpace = measuredWidth - hPadding - mHorizontalSpacing * (mColumnCount - 1);
            int childWidth = MeasureSpec.getSize(childWidthMeasureSpec);
            if (mColumnCount > 0 && childWidth * mColumnCount > childHorizontalSpace) {
                childWidth = childHorizontalSpace / mColumnCount;
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth,
                        MeasureSpec.getMode(childWidthMeasureSpec));
            }

            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, vPadding, lp.height);
            if (mChildHeight > 0) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            columnHeight = Math.max(columnHeight, child.getMeasuredHeight());

        }

        height += columnHeight;

        setMeasuredDimension(widthMeasureSpec, makeMeasureSpec(height, MeasureSpec.EXACTLY));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int columnHeight = 0;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
                continue;

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            columnHeight = Math.max(columnHeight, childHeight);

            child.layout(childLeft, childTop, childLeft + childWidth, childTop
                    + childHeight);

            if (mColumnCount > 0 && (i + 1) % mColumnCount == 0) {
                childLeft = paddingLeft;
                childTop += columnHeight + mVerticalSpacing;
                columnHeight = 0;
            } else {
                childLeft += childWidth + mHorizontalSpacing;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        drawDivider(canvas);
    }

    private void drawDivider(Canvas canvas) {
        if (mDividerSize > 0 && mDividerColor != Color.TRANSPARENT) {
            //绘制水平线
            drawHorizontalLines(canvas);
            //绘制垂直线
            drawVerticalLines(canvas);
        }
    }

    private void drawHorizontalLines(Canvas canvas) {
        int hPadding = (mHorizontalSpacing + mDividerSize) / 2;
        int vPadding = mVerticalSpacing / 2;

        int childCount = getChildCount();
        int rowCount = childCount / mColumnCount + (childCount % mColumnCount > 0 ? 1 : 0);
        for (int i = 0; i < rowCount - 1 || mDrawEdge && i == rowCount - 1; i++) {
            View leftChild = getChildAt(i * mColumnCount);
            int column = Math.min(childCount - mColumnCount * i, mColumnCount);
            View rightChild = getChildAt(i * mColumnCount + column - 1);

            int left = leftChild.getLeft() - hPadding;
            int right = rightChild.getRight() + hPadding;
            int bottom = leftChild.getBottom() + vPadding;
            if (mDrawEdge && i == 0) {
                int top = leftChild.getTop() - vPadding;
                canvas.drawLine(left, top, right, top, mPaint);
            }
            canvas.drawLine(left, bottom, right, bottom, mPaint);
        }
    }

    private void drawVerticalLines(Canvas canvas) {
        int hPadding = mHorizontalSpacing / 2;
        int vPadding = (mVerticalSpacing + mDividerSize) / 2;

        int childCount = getChildCount();
        int columnCount = Math.min(mColumnCount, childCount);
        for (int i = 0; i < columnCount && (mDrawEdge || i < mColumnCount - 1); i++) {
            View topChild = getChildAt(i);
            View bottomChild = topChild;
            int row = childCount / columnCount + (childCount % columnCount > i ? 1 : 0);
            if (row > 0) {
                bottomChild = getChildAt(i + (row - 1) * columnCount);
            }

            int right = topChild.getRight() + hPadding;
            int top = topChild.getTop() - vPadding;
            int bottom = bottomChild.getBottom() + vPadding;
            if (mDrawEdge && i == 0) {
                int left = topChild.getLeft() - hPadding;
                canvas.drawLine(left, top, left, bottom, mPaint);
            }
            canvas.drawLine(right, top, right, bottom, mPaint);

        }
    }
}
