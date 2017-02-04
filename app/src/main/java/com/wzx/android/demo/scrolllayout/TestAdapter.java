package com.wzx.android.demo.scrolllayout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wzx.android.demo.GlobalAnimationHelper;
import com.wzx.android.demo.v2.R;

import java.util.List;

/**
 * Created by wangzhenxing on 17/2/4.
 */

public class TestAdapter extends ArrayAdapter<String> implements View.OnClickListener {

    private final LayoutInflater mInflater;

    public TestAdapter(Context context) {
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.test_list_item, parent, false);
            holder = new ViewHolder();
            holder.mText = (TextView) convertView.findViewById(R.id.text);
            holder.mButton = (Button) convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String title = getItem(position);
        holder.mText.setText(title);
        holder.mButton.setOnClickListener(this);

        return convertView;
    }

    private static class ViewHolder {
        TextView mText;
        Button mButton;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.applaud_animation);
        GlobalAnimationHelper.getInstance((Activity) v.getContext()).startAnimation(v, animation);
    }
}


