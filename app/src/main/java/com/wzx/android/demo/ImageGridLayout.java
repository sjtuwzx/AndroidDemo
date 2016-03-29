package com.wzx.android.demo;

import static android.view.View.MeasureSpec.makeMeasureSpec;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

public class ImageGridLayout extends ViewGroup implements View.OnClickListener {

    private static final String TAG = ImageGridLayout.class.getSimpleName();

    private static final ViewGroup.LayoutParams DEFAULT_LAYOUT_PARAMS = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private ListAdapter mAdapter;
    private AdapterDataSetObserver mDataSetObserver = new AdapterDataSetObserver();
    private RecycleBin mRecycleBin = new RecycleBin();

    private int mColumnCount = 3;
    private int mChildWidth = 300;
    private int mChildHeight = 300;

    private int mHorizontalSpacing = 0;
    private int mVerticalSpacing = 0;

    private Paint mPaint = new Paint();

    public ImageGridLayout(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public ImageGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public ImageGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        mPaint.setColor(0xffff0000);
        mPaint.setStrokeWidth(5);
    }

    private class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            // TODO Auto-generated method stub
            super.onChanged();
            refreshChildren();
        }

        @Override
        public void onInvalidated() {
            // TODO Auto-generated method stub
            super.onInvalidated();
            refreshChildren();
        }
    }

    public void setChildSize(int width, int height) {
        mChildWidth = width;
        mChildHeight = height;
        requestLayout();
    }

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
        refreshChildren();
    }

    private void refreshChildren() {
        removeAllChildren();
        if (mAdapter != null) {
            int childCount = mAdapter.getCount();
            for (int i = 0; i < childCount; i++) {
                View scrapView = mRecycleBin == null ? null : mRecycleBin
                        .getScrapView();
                View childView = mAdapter.getView(i, scrapView, this);
                childView.setOnClickListener(this);

                LayoutParams layoutParams = childView.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = DEFAULT_LAYOUT_PARAMS;
                }
                addViewInLayout(childView, -1, layoutParams);

                if (scrapView != null && scrapView != childView) {
                    mRecycleBin.addScrapView(scrapView);
                }
            }
        }
        requestLayout();
        invalidate();
    }

    private void removeAllChildren() {
        // TODO Auto-generated method stub
        if (mRecycleBin != null) {
            int N = getChildCount();
            for (int i = 0; i < N; i++) {
                View child = getChildAt(i);
                child.setOnClickListener(null);
                mRecycleBin.addScrapView(child);
            }
        }
        removeAllViewsInLayout();
    }

    public void setRecycleBin(RecycleBin recycleBin) {
        if (recycleBin == null || mRecycleBin == recycleBin) {
            return;
        }
        View scrap = null;
        while ((scrap = mRecycleBin.getScrapView()) != null) {
            recycleBin.addScrapView(scrap);
        }
        mRecycleBin = recycleBin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int height = vPadding;

        int columnHeight = 0;
        for (int i = 0, N = getChildCount(); i < N; i++) {
            if (mColumnCount > 0 && i != 0 && i % mColumnCount == 0) {
                height += columnHeight + mVerticalSpacing;
                columnHeight = 0;
            }
            View child = getChildAt(i);
            int childWidth = mChildWidth > 0 ? mChildWidth : child
                    .getMeasuredWidth();
            int childHeight = mChildHeight > 0 ? mChildHeight : child
                    .getMeasuredHeight();

            int childHorizontalSpace = measuredWidth - hPadding
                    - mHorizontalSpacing * (mColumnCount - 1);
            if (mColumnCount > 0
                    && childWidth * mColumnCount > childHorizontalSpace) {
                childWidth = childHorizontalSpace / mColumnCount;
            }

            child.measure(makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));

            columnHeight = Math.max(columnHeight, childHeight);

        }

        height += columnHeight;
        super.onMeasure(widthMeasureSpec, makeMeasureSpec(height, MeasureSpec.EXACTLY));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int childLeft = paddingLeft;
        int childTop = paddingTop;
        for (int i = 0, N = getChildCount(); i < N; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
                continue;

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            child.layout(childLeft, childTop, childLeft + childWidth, childTop
                    + childHeight);
            if (mColumnCount > 0 && (i + 1) % mColumnCount == 0) {
                childLeft = paddingLeft;
                childTop += childHeight + mVerticalSpacing;
            } else {
                childLeft += childWidth + mHorizontalSpacing;
            }
        }
    }

    public static class RecycleBin {

        private ArrayList<View> mScrapViewList = new ArrayList<View>();

        View getScrapView() {
            if (mScrapViewList.size() > 0) {
                return mScrapViewList.remove(0);
            }
            return null;
        }

        void addScrapView(View scrap) {
            mScrapViewList.add(scrap);
        }

    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mOnItemClickListener != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child == v) {
                    mOnItemClickListener.onItemClick(null, v, i, 0);
                    break;
                }
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //绘制水平线
        drawHorizontalLines(canvas);
        //绘制垂直线
        drawVerticalLines(canvas);
    }

    private void drawHorizontalLines(Canvas canvas) {
        int childCount = getChildCount();
        int rowCount = childCount / mColumnCount + (childCount % mColumnCount > 0 ? 1 : 0);
        for (int i = 0; i < rowCount; i++) {
            View leftChild = getChildAt(i * mColumnCount);
            int column = Math.min(childCount - mColumnCount * i, mColumnCount);
            View rightChild = getChildAt(i * mColumnCount + column - 1);

            int hPadding = (mHorizontalSpacing + 5) / 2;
            int vPadding = mVerticalSpacing / 2;
            int left = leftChild.getLeft() - hPadding;
            int right = rightChild.getRight() + hPadding;
            int top = leftChild.getTop() - vPadding;
            int bottom = leftChild.getBottom() + vPadding;
            if (i == 0) {
                canvas.drawLine(left, top, right, top, mPaint);
            }
            canvas.drawLine(left, bottom, right, bottom, mPaint);
        }
    }

    private void drawVerticalLines(Canvas canvas) {
        int childCount = getChildCount();
        int columnCount = Math.min(mColumnCount, childCount);
        for (int i = 0; i < columnCount; i++) {
            View topChild = getChildAt(i);
            View bottomChild = topChild;
            int row = childCount / columnCount + (childCount % columnCount > i ? 1 : 0);
            if (row > 0) {
                bottomChild = getChildAt(i + (row - 1) * columnCount);
            }
            int hPadding = mHorizontalSpacing / 2;
            int vPadding = (mVerticalSpacing + 5) / 2;

            int left = topChild.getLeft() - hPadding;
            int right = topChild.getRight() + hPadding;
            int top = topChild.getTop() - vPadding;
            int bottom = bottomChild.getBottom() + vPadding;
            if (i == 0) {
                canvas.drawLine(left, top, left, bottom, mPaint);
            }
            canvas.drawLine(right, top, right, bottom, mPaint);
        }
    }
}
