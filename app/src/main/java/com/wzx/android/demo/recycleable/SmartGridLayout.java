package com.wzx.android.demo.recycleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;

import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by wang_zx on 2016/1/18.
 */
public class SmartGridLayout extends RecycleBaseLayout {

    private List<ViewLine> mViewLines = new ArrayList<ViewLine>();

    private int mColumnCount = 1;
    private int mHorizontalSpacing = 0;
    private int mVerticalSpacing = 0;

    private Paint mPaint = new Paint();
    private int mDividerColor;
    private int mDividerSize;
    private boolean mDrawEdge = false;
    private int mDividerVerticalPadding = 0;

    private ArrayMap<Integer, Integer[]> mLayoutConfigures = new ArrayMap<Integer, Integer[]>();

    public SmartGridLayout(Context context) {
        this(context, null);
    }

    public SmartGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmartGridLayout, defStyle, 0);
        mColumnCount = a.getInt(R.styleable.SmartGridLayout_column_count, 1);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.SmartGridLayout_horizontal_spacing, 0);
        mVerticalSpacing = a.getDimensionPixelSize(R.styleable.SmartGridLayout_vertical_spacing, 0);

        mDividerColor = a.getColor(R.styleable.SmartGridLayout_dividerColor, Color.TRANSPARENT);
        mDividerSize = a.getDimensionPixelSize(R.styleable.SmartGridLayout_dividerSize, 0);
        mDrawEdge = a.getBoolean(R.styleable.SmartGridLayout_drawEdge, false);
        mDividerVerticalPadding = a.getDimensionPixelSize(R.styleable.SmartGridLayout_divider_vertical_padding, 0);

        a.recycle();

        mPaint.setColor(mDividerColor);
        mPaint.setStrokeWidth(mDividerSize);
    }

    public void addLayoutConfigure(int childrenCount, Integer[] configure) {
        mLayoutConfigures.put(childrenCount, configure);
    }

    public void clearLayoutConfigure() {
        mLayoutConfigures.clear();
    }

    @Override
    protected void removeAllChildren() {
        super.removeAllChildren();
        mViewLines.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int hPadding = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop();
        int width = MeasureSpec.getSize(widthMeasureSpec);

        mViewLines.clear();
        Integer[] lineChildCounts = calculateChildCountForLines(getChildCount());
        int childIndexOffset = 0;
        int lineTop = paddingTop;
        for (int lineChildCount : lineChildCounts) {
            ViewLine line = new ViewLine();
            line.top = lineTop;
            mViewLines.add(line);

            int childrenHorizontalSpace = width - hPadding - mHorizontalSpacing * (lineChildCount - 1);
            int childWidth = (childrenHorizontalSpace) / lineChildCount;
            int columnHeight = 0;
            for (int i = 0; i < lineChildCount; i++) {
                View child = getChildAt(childIndexOffset + i);
                line.children.add(child);

                if (i == lineChildCount - 1) {
                    childWidth = childrenHorizontalSpace - childWidth * (lineChildCount - 1);
                }
                measureView(child, makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.UNSPECIFIED);
                int childHeight = child.getMeasuredHeight();
                columnHeight = Math.max(columnHeight, childHeight);
            }
            line.height = columnHeight;
            lineTop += columnHeight + mVerticalSpacing;
            childIndexOffset += lineChildCount;
        }

        int height = lineTop + getPaddingBottom() - (lineTop > paddingTop ? mVerticalSpacing : 0);

        super.onMeasure(widthMeasureSpec, makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private static void measureView(View view, int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height);

        view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private Integer[] calculateChildCountForLines(int childCount) {
        if (mLayoutConfigures.containsKey(childCount)) {
            return mLayoutConfigures.get(childCount);
        }
        int rowCount = childCount / mColumnCount + +(childCount % mColumnCount > 0 ? 1 : 0);
        Integer[] lineChildCounts = new Integer[rowCount];
        int minColumnCount = 0;
        int remainCount = 0;
        if (rowCount > 0) {
            minColumnCount = childCount / rowCount;
            remainCount = childCount % rowCount;
        }
        for (int i = 0; i < lineChildCounts.length; i++) {
            lineChildCounts[i] = minColumnCount + (i < remainCount ? 1 : 0);
        }

        return lineChildCounts;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        for (ViewLine line : mViewLines) {
            int childLeft = getPaddingLeft();
            for (View child : line.children) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                child.layout(childLeft, line.top, childLeft + childWidth, line.top + childHeight);

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

        int lineCount = mViewLines.size();
        for (int i = 0; i < lineCount - 1 || mDrawEdge && i == lineCount - 1; i++) {
            ViewLine line = mViewLines.get(i);
            List<View> children = line.children;
            View leftChild = children.get(0);
            View rightChild = children.get(children.size() - 1);

            int left = leftChild.getLeft() - hPadding;
            int right = rightChild.getRight() + hPadding;
            int top = leftChild.getTop() - vPadding;
            int bottom = line.top + line.height + vPadding;
            if (mDrawEdge && i == 0) {
                canvas.drawLine(left, top, right, top, mPaint);
            }
            canvas.drawLine(left, bottom, right, bottom, mPaint);
        }
    }

    private void drawVerticalLines(Canvas canvas) {
        int hPadding = mHorizontalSpacing / 2;
        int vPadding = (mVerticalSpacing + mDividerSize) / 2 - mDividerVerticalPadding;

        for (ViewLine line : mViewLines) {
            if (line.height + vPadding * 2 <= 0) {
                continue;
            }
            List<View> children = line.children;
            int N = children.size();
            for (int i = 0; i < N - 1 || mDrawEdge && i == N - 1; i++) {
                View child = children.get(i);

                int right = child.getRight() + hPadding;
                int top = line.top - vPadding;
                int bottom = line.top + line.height + vPadding;
                if (mDrawEdge && i == 0) {
                    int left = child.getLeft() - hPadding;
                    canvas.drawLine(left, top, left, bottom, mPaint);
                }
                canvas.drawLine(right, top, right, bottom, mPaint);
            }

        }
    }

    private static class ViewLine {
        int top = 0;
        int height = 0;
        List<View> children = new ArrayList<View>();
    }
}
