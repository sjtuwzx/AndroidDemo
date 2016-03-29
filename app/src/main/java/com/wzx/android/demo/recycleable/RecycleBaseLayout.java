package com.wzx.android.demo.recycleable;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * Created by wang_zx on 2016/1/19.
 */
public abstract class RecycleBaseLayout extends ViewGroup implements View.OnClickListener {

    private static final LayoutParams DEFAULT_LAYOUT_PARAMS = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    private ListAdapter mAdapter;
    private AdapterDataSetObserver mDataSetObserver = new AdapterDataSetObserver();
    private RecycleBaseLayout.RecycleBin mRecycleBin = new RecycleBaseLayout.RecycleBin();

    public RecycleBaseLayout(Context context) {
        super(context);
    }

    public RecycleBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleBaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    protected void removeAllChildren() {
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

    public void setRecycleBin(RecycleBaseLayout.RecycleBin recycleBin) {
        if (recycleBin == null || mRecycleBin == recycleBin) {
            return;
        }
        View scrap;
        while ((scrap = mRecycleBin.getScrapView()) != null) {
            recycleBin.addScrapView(scrap);
        }
        mRecycleBin = recycleBin;
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

    public interface OnItemClickListener {
        void onItemClick(ListAdapter adapter, View v, int position);
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
                    mOnItemClickListener.onItemClick(mAdapter, v, i);
                    break;
                }
            }
        }
    }
}
