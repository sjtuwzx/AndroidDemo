package com.wzx.android.demo.pinnedHeader;

import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wang_zx on 2015/7/10.
 */
public class SectionedListCommonAdapter extends SectionedListAdapter {

    private static final int DEFAULT_TYPE_COUNT_HEADER = 10;

    private static final int DEFAULT_TYPE_COUNT_ITEM = 10;

    private int mHeaderTypeCount;
    private int mItemTypeCount;

    private int mCurrentMaxHeaderType = 0;
    private int mCurrentMaxItemType = -1;
    private Map<Class<?>, Integer> mHeaderTypeCache = new HashMap<Class<?>, Integer>();
    private Map<Class<?>, Integer> mItemTypeOffsetCache = new HashMap<Class<?>, Integer>();

    public SectionedListCommonAdapter() {
        this(DEFAULT_TYPE_COUNT_HEADER, DEFAULT_TYPE_COUNT_ITEM);
    }

    public SectionedListCommonAdapter(int headerTypeCount, int itemTypeCount) {
        mHeaderTypeCount = headerTypeCount;
        mItemTypeCount = itemTypeCount;
    }

    @Override
    public int getSectionHeaderViewTypeCount() {
        return mHeaderTypeCount;
    }

    @Override
    public int getItemViewTypeCount() {
        return mItemTypeCount;
    }

    @Override
    public int getSectionHeaderViewType(int section) {
        AdapterInfo adapterInfo = getAdapterInfo(section);
        AdapterInfo.HeaderCreator headerCreator = adapterInfo.mHeaderCreator;
        if (headerCreator != null) {
            Class<?> headerCreatorClass = headerCreator.getClass();
            if (!mHeaderTypeCache.containsKey(headerCreatorClass)) {
                ++mCurrentMaxHeaderType;
                mHeaderTypeCache.put(headerCreatorClass, mCurrentMaxHeaderType);
            }
            return mHeaderTypeCache.get(headerCreatorClass);
        }
        return 0;
    }

    @Override
    public int getItemViewType(int section, int position) {
        AdapterInfo adapterInfo = getAdapterInfo(section);
        BaseAdapter adapter = adapterInfo.mAdapter;
        Class<?> adapterClass = adapter.getClass();
        if (!mItemTypeOffsetCache.containsKey(adapterClass)) {
            mItemTypeOffsetCache.put(adapterClass, mCurrentMaxItemType + 1);
            mCurrentMaxItemType += adapter.getViewTypeCount();
        }
        int itemTypeOffset = mItemTypeOffsetCache.get(adapterClass);
        return itemTypeOffset + adapter.getItemViewType(position);
    }
}
