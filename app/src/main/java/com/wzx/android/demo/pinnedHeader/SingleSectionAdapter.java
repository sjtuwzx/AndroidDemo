package com.wzx.android.demo.pinnedHeader;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/3/11.
 */
public class SingleSectionAdapter extends SingleSectionBaseAdapter {

    private final LayoutInflater mInflater;

    public SingleSectionAdapter(Context context) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getHeaderCount() {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public View getPinnedHeaderView(int position, View convertView, ViewGroup parent) {
        return makeView(String.format("header[%d]", position + 1), convertView, parent, true);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        return makeView(String.format("item[%d]", position + 1), convertView, parent, false);
    }

    public View makeView(String title, View convertView, ViewGroup parent, boolean isHeader) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.mText = (TextView) convertView.findViewById(R.id.text);
            holder.mButton = (Button) convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (isHeader) {
            convertView.setBackgroundColor(0xFFFFFFFF);
        }
        holder.mText.setText("");
        holder.mText.setFocusable(false);
        holder.mButton.setFocusable(false);

        if (TextUtils.isEmpty(title)) {
            return convertView;
        }
        holder.mText.setText(title);

        return convertView;
    }

    private static class ViewHolder {
        TextView mText;
        Button mButton;
    }
}
