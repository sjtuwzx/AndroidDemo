package com.wzx.android.demo.slidingremove;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * Created by wang_zx on 2016/1/19.
 *
 * Item 垮控件复用时OnItemClickListener设置可能会异常
 */
public abstract class RecycleBaseLayout extends ViewGroup implements View.OnClickListener {

    private ListAdapter mAdapter;
    private AdapterDataSetObserver mDataSetObserver = new AdapterDataSetObserver();
    private RecycleBin mRecycleBin = new RecycleBin();
    private Handler mHandler = new Handler();

    private final boolean[] mIsScrap = new boolean[1];

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

            mHandler.removeCallbacks(mRefreshChildrenRunnable);
            mHandler.post(mRefreshChildrenRunnable);
        }

        @Override
        public void onInvalidated() {
            // TODO Auto-generated method stub
            super.onInvalidated();

            mHandler.removeCallbacks(mRefreshChildrenRunnable);
            mHandler.post(mRefreshChildrenRunnable);
        }
    }

    private Runnable mRefreshChildrenRunnable = new Runnable() {
        @Override
        public void run() {
            refreshChildren();
        }
    };

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter == adapter) {
            return;
        }
        mRecycleBin.clear();
        removeAllViews();

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
            for (int position = 0; position < childCount; position++) {
                View child = obtainView(position, mIsScrap);

                if (mIsScrap[0]) {
                    attachViewToParent(child, -1, child.getLayoutParams());
                } else {
                    addViewInLayout(child, -1, child.getLayoutParams(), true);
                }
            }
        }

        requestLayout();
        invalidate();
    }

    private View obtainView(int position, boolean[] isScrap) {
        int viewType = mAdapter.getItemViewType(position);
        View scrapView = mRecycleBin == null ? null
                : mRecycleBin .getScrapView(viewType);

        View child = mAdapter.getView(position, scrapView, this);
        setItemViewLayoutParams(child, position);

        if (mOnItemClickListener != null && !child.hasOnClickListeners()) {
            child.setOnClickListener(this);
        }

        if (scrapView != null && scrapView != child) {
            mRecycleBin.addScrapView(scrapView);
        }

        isScrap[0] = scrapView == child;

        return child;
    }

    protected void removeAllChildren() {
        // TODO Auto-generated method stub
        if (mRecycleBin != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                mRecycleBin.addScrapView(child);
            }
        }
        detachAllViewsFromParent();
    }

    private void setItemViewLayoutParams(View child, int position) {
        final ViewGroup.LayoutParams vlp = child.getLayoutParams();
        LayoutParams lp;
        if (vlp == null) {
            lp = (LayoutParams) generateDefaultLayoutParams();
        } else if (!checkLayoutParams(vlp)) {
            lp = (LayoutParams) generateLayoutParams(vlp);
        } else {
            lp = (LayoutParams) vlp;
        }

        lp.mViewType = mAdapter.getItemViewType(position);
        if (lp != vlp) {
            child.setLayoutParams(lp);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
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

    public static class LayoutParams extends ViewGroup.LayoutParams {

        int mViewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            mViewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }

    public void setRecycleBin(RecycleBin recycleBin) {
        if (recycleBin == null || mRecycleBin == recycleBin) {
            return;
        }
        mRecycleBin.into(recycleBin);
        mRecycleBin.clear();
        mRecycleBin = recycleBin;
    }

    public static class RecycleBin {

        private SparseArray<ArrayList<View>> mScrapViewsMap = new SparseArray<>();

        private View getScrapView(int viewType) {
            if (viewType >= 0) {
                ArrayList<View> scrapViews = mScrapViewsMap.get(viewType);
                if (scrapViews != null && scrapViews.size() > 0) {
                    View scrapView = scrapViews.remove(0);
                    scrapView.layout(0, 0, 0, 0);
                    return scrapView;
                }
            }
            return null;
        }

        private void addScrapView(View scrap) {
            final LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
            if (lp == null) {
                return;
            }
            int viewType = lp.mViewType;
            addScrapView(scrap, viewType);
        }

        private void addScrapView(View scrap, int viewType) {
            if (viewType >= 0) {
                if (mScrapViewsMap.indexOfKey(viewType) < 0) {
                    mScrapViewsMap.put(viewType, new ArrayList<View>());
                }
                ArrayList<View> scrapViews = mScrapViewsMap.get(viewType);
                scrapViews.add(scrap);
            }
        }

        private void clear() {
            mScrapViewsMap.clear();
        }

        private void into(RecycleBin recycleBin) {
            for (int i = 0, size = mScrapViewsMap.size(); i < size; i++) {
                int viewType = mScrapViewsMap.keyAt(i);
                ArrayList<View> scrapViews = mScrapViewsMap.get(viewType);
                for (View scrap : scrapViews) {
                    recycleBin.addScrapView(scrap, viewType);
                }
            }
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
