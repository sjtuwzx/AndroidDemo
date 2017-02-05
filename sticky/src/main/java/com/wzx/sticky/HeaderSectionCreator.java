package com.wzx.sticky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wzx.sticky.listview.SectionedListAdapter;

/**
 * Created by wangzhenxing on 16/12/10.
 */

public class HeaderSectionCreator extends SectionedListAdapter.SectionInfo.HeaderCreator {

    private LayoutInflater mInflater;
    private int mIndex;

    public HeaderSectionCreator(Context context, int index) {
        mInflater = LayoutInflater.from(context);
        mIndex = index;
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.header_layout, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        textView.setText(String.format("header[%d]", mIndex));

        return convertView;
    }
}
