package com.wzx.sticky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wzx.sticky.listview.SectionedListAdapter;

/**
 * Created by wangzhenxing on 16/12/10.
 */

public class MainSectionHeaderCreator extends SectionedListAdapter.SectionInfo.HeaderCreator {

    private LayoutInflater mInflater;

    public MainSectionHeaderCreator(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_header_layout, parent, false);
        }
        return convertView;
    }
}
