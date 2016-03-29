package com.wzx.android.demo;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

import java.util.List;

/**
 * Created by wang_zx on 2015/5/28.
 */
public class HotelOrderPostAdapter extends ArrayAdapter<HotelStatusItemModel> {

    private LayoutInflater mInflater;

    public HotelOrderPostAdapter(Context context) {
        super(context, -1);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<HotelStatusItemModel> statusItemModels) {
        clear();
        if (statusItemModels != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                addAll(statusItemModels);
            } else {
                for (HotelStatusItemModel itemModel : statusItemModels) {
                    add(itemModel);
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.hotel_order_status_item , parent, false);
            holder = new ViewHolder();
            holder.progressRoundImage = (ImageView) convertView.findViewById(R.id.progress_round);
            holder.progressLineTopView = convertView.findViewById(R.id.progress_line_top);
            holder.progressLineBottomView = convertView.findViewById(R.id.progress_line_bottom);
            holder.descriptionText = (TextView) convertView.findViewById(R.id.text_description);
            holder.timeText = (TextView) convertView.findViewById(R.id.text_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HotelStatusItemModel item = getItem(position);
        if (position == 0) {
            holder.progressLineTopView.setVisibility(View.INVISIBLE);
            holder.progressRoundImage.setBackgroundResource(R.drawable.hotel_top_current);
            holder.descriptionText.setTextColor(0xFFFFFFFF);
            holder.timeText.setTextColor(0xFFCAEAFD);
        } else {
            holder.progressLineTopView.setVisibility(View.VISIBLE);
            holder.progressRoundImage.setBackgroundResource(R.drawable.hotel_top_current_out);
            holder.descriptionText.setTextColor(0xFF99CEF2);
            holder.timeText.setTextColor(0xFF99CEF2);
        }
        if (position == getCount() - 1) {
            holder.progressLineBottomView.setVisibility(View.INVISIBLE);
        } else {
            holder.progressLineBottomView.setVisibility(View.VISIBLE);
        }
        holder.descriptionText.setText(item.description);
        holder.timeText.setText(item.time);

        return convertView;
    }

    private static class ViewHolder {
        ImageView progressRoundImage;
        View progressLineTopView;
        View progressLineBottomView;
        TextView descriptionText;
        TextView timeText;
    }
}

