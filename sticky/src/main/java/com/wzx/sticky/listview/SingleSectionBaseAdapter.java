package com.wzx.sticky.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SingleSectionBaseAdapter extends BaseAdapter implements
        PinnedSingleSectionHeadersListView.PinnedSingleSectionedHeaderAdapter {

    private static int HEADER_VIEW_TYPE = 0;
    private static int ITEM_VIEW_TYPE = 0;

    /**
     * Caches the item count
     */
    private int mCount;

    public SingleSectionBaseAdapter() {
        super();
        mCount = -1;
    }

    @Override
    public void notifyDataSetChanged() {
        mCount = -1;
        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onChange(this);
        }
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        mCount = -1;
        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onChange(this);
        }
        super.notifyDataSetInvalidated();
    }

    @Override
    public final int getCount() {
        if (mCount < 0) {
            mCount = getHeaderCount() + getItemCount();
        }
        return mCount;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (isSectionHeader(position)) {
            return getPinnedHeaderView(position, convertView, parent);
        }
        return getItemView(position - getHeaderCount(), convertView, parent);
    }

    @Override
    public final int getItemViewType(int position) {
        if (isSectionHeader(position)) {
            return getItemViewTypeCount()
                    + getHeaderViewType(position);
        }
        return getItemType(position - getHeaderCount());
    }

    @Override
    public final int getViewTypeCount() {
        return getItemViewTypeCount() + getHeaderViewTypeCount();
    }

    public final boolean isSectionHeader(int position) {
        int headerCount = getHeaderCount();
        return position < headerCount;
    }

    public int getItemType(int position) {
        return ITEM_VIEW_TYPE;
    }

    public int getItemViewTypeCount() {
        return 1;
    }

    public int getHeaderViewType(int position) {
        return HEADER_VIEW_TYPE;
    }

    public int getHeaderViewTypeCount() {
        return 1;
    }

    public abstract int getItemCount();

    public abstract View getItemView(int position,
                                     View convertView, ViewGroup parent);

    private OnDataChangeListener mOnDataChangeListener;

    @Override
    public void setOnDataChangeListener(OnDataChangeListener listener) {
        mOnDataChangeListener = listener;
    }

    static interface OnDataChangeListener {
        void onChange(SingleSectionBaseAdapter adapter);
    }



}
