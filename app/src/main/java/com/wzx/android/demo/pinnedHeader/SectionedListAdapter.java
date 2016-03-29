package com.wzx.android.demo.pinnedHeader;

import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.wzx.android.demo.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SectionedListAdapter extends SectionedBaseAdapter {
    public static final String TAG = SectionedListAdapter.class.getSimpleName();

    protected List<AdapterInfo> mAdapters = new ArrayList<AdapterInfo>();

    private int mItemViewTypeCount = -1;
    private SparseArrayCompat<Boolean> mSectionHasHeaderCache = new SparseArrayCompat<Boolean>();
    private SparseArrayCompat<Integer> mSectionFirstItemViewTypeCache = new SparseArrayCompat<Integer>();

    private View mCurrentPinnedHeaderView;
    private int mCurrentPinnedSection = -1;

    private View mCurrentDoublePinnedHeaderView;
    private int mCurrentDoublePinnedSection = -1;

    public void addAdapterInfo(AdapterInfo adapterInfo) {
        mAdapters.add(adapterInfo);
        notifyDataSetChanged();
    }

    public void addAdapterInfo(int index, AdapterInfo adapterInfo) {
        mAdapters.add(index, adapterInfo);
        notifyDataSetChanged();
    }

    public void removeAllAdapterInfo() {
        mAdapters.clear();
        notifyDataSetChanged();
    }

    public void clean() {
        mAdapters.clear();
        notifyDataSetChanged();
    }

    public AdapterInfo getAdapterInfoForPosition(int position) {
        int section = getSectionForPosition(position);
        return mAdapters.get(section);
    }

    @Override
    public Object getItem(int section, int position) {
        // TODO Auto-generated method stub
        if (section < mAdapters.size()) {
            AdapterInfo info = mAdapters.get(section);
            BaseAdapter adapter = info.mAdapter;
            if (adapter != null && position >= 0) {
                return adapter.getItem(position);
            }
        }
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        // TODO Auto-generated method stub
        if (section < mAdapters.size()) {
            AdapterInfo info = mAdapters.get(section);
            BaseAdapter adapter = info.mAdapter;
            if (adapter != null && position >= 0) {
                return adapter.getItemId(position);
            }
        }
        return -1;
    }

    @Override
    public int getSectionCount() {
        // TODO Auto-generated method stub
        return mAdapters.size();
    }

    @Override
    public int getCountForSection(int section) {
        // TODO Auto-generated method stub
        if (section < mAdapters.size()) {
            AdapterInfo info = mAdapters.get(section);
            if (info.mIsExpanded) {
                BaseAdapter adapter = info.mAdapter;
                if (adapter != null)
                    return adapter.getCount();
            }
        }
        return 0;
    }

    @Override
    public View getItemView(int section, int position, View convertView,
                            ViewGroup parent) {
        // TODO Auto-generated method stub
        if (section < mAdapters.size()) {
            AdapterInfo info = mAdapters.get(section);
            BaseAdapter adapter = info.mAdapter;
            if (adapter != null)
                return adapter.getView(position, convertView, parent);
        }
        return null;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView,
                                     ViewGroup parent) {
        // TODO Auto-generated method stub
        if (section < mAdapters.size() && hasSectionHeader(section)) {
            return createSectionHeader(section, convertView, parent);
        }
        return null;
    }

    @Override
    public View getSectionPinnedHeaderView(int section, View convertView, ViewGroup parent) {
        if (mCurrentPinnedSection != section) {
            mCurrentPinnedHeaderView = createSectionHeader(section, convertView, parent);
            mCurrentPinnedSection = section;
        }
        return mCurrentPinnedHeaderView;
    }

    private View createSectionHeader(int section, View convertView,
                                     ViewGroup parent) {
        AdapterInfo info = mAdapters.get(section);

        if (info.mHeaderCreator != null) {
            return info.mHeaderCreator.onHeaderCreate(convertView, parent);
        } else if (info.mHeaderView != null) {
            return info.mHeaderView;
        }
        return null;
    }


    @Override
    public boolean hasSectionHeader(int section) {
        // TODO Auto-generated method stub
        if (section < mAdapters.size()) {
            if (mSectionHasHeaderCache.indexOfKey(section) < 0) {
                AdapterInfo info = mAdapters.get(section);
                mSectionHasHeaderCache.put(section, info.hasHeader());
            }
            return mSectionHasHeaderCache.get(section);
        }
        return false;
    }

    public AdapterInfo getAdapterInfo(int section) {
        if (section >= 0 && section < mAdapters.size()) {
            return mAdapters.get(section);
        }
        return null;
    }

    @Override
    public int getSectionHeaderViewTypeCount() {
        return getSectionCount();
    }

    @Override
    public int getItemViewTypeCount() {
        if (mItemViewTypeCount >= 0) {
            return mItemViewTypeCount;
        }
        mItemViewTypeCount = 0;
        int N = mAdapters.size();
        for (int i = 0; i < N; i++) {
            mSectionFirstItemViewTypeCache.put(i, mItemViewTypeCount);
            AdapterInfo adapterInfo = mAdapters.get(i);
            BaseAdapter adapter = adapterInfo.mAdapter;
            mItemViewTypeCount += adapter.getViewTypeCount();
        }
        return mItemViewTypeCount;
    }

    @Override
    public int getSectionHeaderViewType(int section) {
        return section;
    }

    @Override
    public int getItemViewType(int section, int position) {
        getItemViewTypeCount();
        int sectionFirstItemViewType = mSectionFirstItemViewTypeCache.get(section, 0);
        AdapterInfo adapterInfo = getAdapterInfo(section);
        BaseAdapter adapter = adapterInfo.mAdapter;
        return sectionFirstItemViewType + adapter.getItemViewType(position);
    }

    @Override
    public boolean shouldPinSectionHeader(int section) {
        if (section < mAdapters.size()) {
            AdapterInfo info = mAdapters.get(section);
            return info.mShouldPinHeader;
        }
        return false;
    }

    @Override
    public View getDoublePinnedHeaderView(int position, ViewGroup parent) {
        int currentSection = getSectionForPosition(position);

        for (int section = currentSection; section >= 0; section--) {
            AdapterInfo adapterInfo = mAdapters.get(section);
            if (adapterInfo.mPinnedDoubleHeader) {
                View convertView = mCurrentDoublePinnedSection == section ? mCurrentDoublePinnedHeaderView : null;
                mCurrentDoublePinnedHeaderView = createSectionHeader(section, convertView, parent);
                mCurrentDoublePinnedSection = section;

                return mCurrentDoublePinnedHeaderView;
            }
        }
        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        reset();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        reset();
        super.notifyDataSetInvalidated();
    }

    protected void reset() {
        mItemViewTypeCount = -1;
        mSectionHasHeaderCache.clear();
        mSectionFirstItemViewTypeCache.clear();
        mCurrentPinnedHeaderView = null;
        mCurrentPinnedSection = -1;
        mCurrentDoublePinnedHeaderView = null;
        mCurrentDoublePinnedSection = -1;
    }

    public static class AdapterInfo {
        BaseAdapter mAdapter;
        boolean mHasHeader = false;
        View mHeaderView;
        HeaderCreator mHeaderCreator;
        boolean mShouldPinHeader = false;
        boolean mIsExpanded = true;
        private boolean mPinnedDoubleHeader = false;

        public boolean hasHeader() {
            if (mHeaderCreator != null) {
                return mHeaderCreator.hasHeader();
            }
            return mHasHeader;
        }

        public HeaderCreator getHeaderCreator() {
            return mHeaderCreator;
        }

        public void setIsExpanded(boolean isExpanded) {
            mIsExpanded = isExpanded;
        }

        public boolean isExpanded() {
            return mIsExpanded;
        }

        public interface HeaderCreator {
            boolean hasHeader();

            View onHeaderCreate(View convertView, ViewGroup parent);
        }

        public static class Builder {
            private BaseAdapter mAdapter;
            private boolean mHasHeader = false;
            private View mHeaderView;
            private HeaderCreator mHeaderCreator;
            private boolean mShouldPinHeader = false;
            private boolean mIsExpanded = true;
            private boolean mPinnedDoubleHeader = false;

            public Builder setAdapter(BaseAdapter adapter) {
                mAdapter = adapter;
                return this;
            }

            @Deprecated
            public Builder setHasHeader(boolean hasHeader) {
                mHasHeader = hasHeader;
                return this;
            }

            @Deprecated
            public Builder setHeaderView(View header) {
                mHeaderView = header;
                mHeaderView.setContentDescription(PinnedHeaderListView.TAG_PINNED_HEADER);
                return this;
            }

            public Builder setHeaderCreator(HeaderCreator creator) {
                mHeaderCreator = creator;
                return this;
            }

            public Builder setShouldPinHeader(boolean shouldPinHeader) {
                mShouldPinHeader = shouldPinHeader;
                return this;
            }

            public Builder setIsExpanded(boolean isExpanded) {
                mIsExpanded = isExpanded;
                return this;
            }

            public Builder pinnedDoubleHeader() {
                mPinnedDoubleHeader = true;
                return this;
            }

            public AdapterInfo create() {
                AdapterInfo adapterInfo = new AdapterInfo();
                adapterInfo.mAdapter = mAdapter != null ? mAdapter : new ArrayAdapter<Object>(MainActivity.sContext, 0);
                adapterInfo.mHasHeader = mHasHeader;
                adapterInfo.mHeaderView = mHeaderView;
                adapterInfo.mHeaderCreator = mHeaderCreator;
                adapterInfo.mShouldPinHeader = mShouldPinHeader;
                adapterInfo.mIsExpanded = mIsExpanded;
                adapterInfo.mPinnedDoubleHeader = mPinnedDoubleHeader;

                return adapterInfo;
            }
        }

    }

}