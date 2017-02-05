package com.wzx.recyclable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhenxing on 17/2/4.
 */

public class ItemAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;

    private RecycleBaseLayout.RecycleBin mImageGridRecycleBin = new RecycleBaseLayout.RecycleBin();

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
            holder.mGridLayout = (RecycleBaseLayout) convertView.findViewById(R.id.grid);
            holder.mGridLayout.setRecycleBin(mImageGridRecycleBin);
            holder.mAdapter = new ImageGridAdapter(getContext());
            holder.mGridLayout.setAdapter(holder.mAdapter);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String title = getItem(position);
        holder.mText.setText(title);
        List<String> imageList = new ArrayList<String>();
        for (int i = 0; i < position % 20; i++) {
            imageList.add(String.format("image%d", i + 1));
        }
        holder.mAdapter.setData(imageList);

        return convertView;
    }

    private static class ViewHolder {
        TextView mText;
        Button mButton;
        RecycleBaseLayout mGridLayout;
        ImageGridAdapter mAdapter;
    }

    public static class ImageGridAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private List<String> mImageList;

        public ImageGridAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<String> data) {
            mImageList = data;
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImageList == null ? 0 : mImageList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.image_item, parent, false);
            }

            return convertView;
        }


    }
}


