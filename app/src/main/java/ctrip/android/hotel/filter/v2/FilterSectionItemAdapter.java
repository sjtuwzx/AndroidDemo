package ctrip.android.hotel.filter.v2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wzx.android.demo.ImageGridLayout;
import com.wzx.android.demo.v2.R;

import java.util.List;

import ctrip.android.hotel.filter.DeviceInfoUtil;
import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;

/**
 * Created by wang_zx on 2015/12/30.
 */
public class FilterSectionItemAdapter extends ArrayAdapter<FilterGroup> {

    private LayoutInflater mInflater;

    public FilterSectionItemAdapter(Context context) {
        super(context, View.NO_ID);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<FilterGroup> groupList) {
        clear();
        if (groupList != null) {
            addAll(groupList);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.filter_section_item, parent, false);
            viewHolder = new ViewHolder(convertView, getContext());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FilterGroup group = getItem(position);
        viewHolder.adapter.setFilterGroup(group);

        return convertView;

    }

    private static class  ViewHolder {
        ImageGridLayout gridLayout;
        FilterItemAdapter adapter;


        public ViewHolder(View view, Context context) {
            gridLayout = (ImageGridLayout) view.findViewById(R.id.grid);
            gridLayout.setChildSize(10000, DeviceInfoUtil.getPixelFromDip(44));

            adapter = new FilterItemAdapter(context);
            gridLayout.setAdapter(adapter);
        }
    }

    private static class FilterItemAdapter extends ArrayAdapter<FilterNode> {

        private LayoutInflater mInflater;

        public FilterItemAdapter(Context context) {
            super(context, View.NO_ID);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setFilterGroup(FilterGroup group) {
            clear();
            List<FilterNode> children = group.getChildren(true);
            addAll(children);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = (TextView) mInflater.inflate(R.layout.hotel_keyword_filter_item, parent, false);
            } else {
                textView = (TextView) convertView;
            }

            FilterNode node = getItem(position);
            textView.setText(node.getDisplayName());

            return textView;
        }
    }
}
