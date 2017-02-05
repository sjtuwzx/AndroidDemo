package com.wzx.scrolllayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wangzhenxing on 17/2/4.
 */

public class ItemAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;

    public ItemAdapter(Context context) {
        super(context, -1);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<String> data) {
        clear();
        if (data != null) {
            for (String item : data)
                add(item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.mText = (TextView) convertView.findViewById(R.id.text);
            holder.mButton = (Button) convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String title = getItem(position);
        holder.mText.setText(title);

        return convertView;
    }

    private static class ViewHolder {
        TextView mText;
        Button mButton;
    }
}


