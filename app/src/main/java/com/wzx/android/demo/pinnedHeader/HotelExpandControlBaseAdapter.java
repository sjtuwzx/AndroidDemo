package com.wzx.android.demo.pinnedHeader;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by wang_zx on 2016/1/9.
 */
public abstract class HotelExpandControlBaseAdapter extends BaseAdapter {

    private BaseAdapter mAdapter;
    private int mMinItemCount;
    protected boolean mExpanded = false;

    public HotelExpandControlBaseAdapter(BaseAdapter adapter, int minItemCount, boolean expanded) {
        mAdapter = adapter;
        mMinItemCount = minItemCount;
        mExpanded = expanded ;
    }
    @Override
    public int getCount() {
        int count = mAdapter.getCount();
        if (count <= mMinItemCount) {
            return count;
        } else if (mExpanded) {
            return count + 1;
        } else {
            return mMinItemCount + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (position < mMinItemCount || mExpanded && position < mAdapter.getCount()) {
            return mAdapter.getItem(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mMinItemCount || mExpanded && position < mAdapter.getCount()) {
            return mAdapter.getItemViewType(position);
        } else {
            return mAdapter.getViewTypeCount();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < mMinItemCount || mExpanded && position < mAdapter.getCount()) {
            return mAdapter.getView(position, convertView, parent);
        } else {
            return getBottomControllerView(position, convertView, parent, mExpanded);
        }
    }

    public abstract View getBottomControllerView(int position, View convertView, ViewGroup parent, boolean expanded);
}
