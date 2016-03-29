package com.wzx.android.demo.pinnedHeader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2016/1/9.
 */
public class HotelExpandControlAdapter extends HotelExpandControlBaseAdapter implements View.OnClickListener {

    private BaseAdapter mParentAdapter;
    private LayoutInflater mInflater;

    public HotelExpandControlAdapter(Context context, BaseAdapter parentAdapter, BaseAdapter adapter, int minItemCount, boolean expanded) {
        super(adapter, minItemCount, expanded);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mParentAdapter = parentAdapter;
    }

    @Override
    public View getBottomControllerView(int position, View convertView, ViewGroup parent, boolean expanded) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_expand_controller, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(expanded ? "收起" : "展开");

        convertView.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        mExpanded ^= true;
        mParentAdapter.notifyDataSetChanged();
    }
}
