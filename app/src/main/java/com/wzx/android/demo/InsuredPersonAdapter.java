package com.wzx.android.demo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

import java.util.List;


/**
 * Created by wang_zx on 2016/2/2.
 */
public class InsuredPersonAdapter extends ArrayAdapter<Object> implements View.OnClickListener, SlidingRemoveView.OnViewRemoveListener {

    private LayoutInflater mInflater;
    private int mCount = 0;

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private OnItemRemoveListener mOnItemRemoveListener;

    public InsuredPersonAdapter(Activity activity) {
        super(activity, View.NO_ID);
        mInflater = activity.getLayoutInflater();
    }

    public void setData(List<Object> insuranceTypeList) {
        clear();
        if (insuranceTypeList != null) {
            addAll(insuranceTypeList);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.hotel_insured_person_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mPosition = position;
        viewHolder.mSlidingRemoveView.reset();
        viewHolder.mSlidingRemoveView.setOnViewRemoveListener(this);

        viewHolder.mContainerView.setTag(position);
        viewHolder.mContainerView.setOnClickListener(this);
        viewHolder.mDeleteButton.setTag(viewHolder.mSlidingRemoveView);
        viewHolder.mDeleteButton.setOnClickListener(this);


        return convertView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_delete) {
            SlidingRemoveView slidingRemoveView = (SlidingRemoveView) v.getTag();
            slidingRemoveView.open();
        } else if (id == R.id.container_view) {
            if (mOnItemClickListener != null) {
                int position = (Integer) v.getTag();
                mOnItemClickListener.onItemClick(null, null, position, 0);
            }
        }
    }

    @Override
    public void onRemoveFinish(SlidingRemoveView slidingRemoveView) {
        if (mOnItemRemoveListener != null) {
            ViewHolder viewHolder = (ViewHolder) slidingRemoveView.getTag();
            mOnItemRemoveListener.onRemove(this, viewHolder.mPosition);
        }

    }

    private static class ViewHolder {

        private SlidingRemoveView mSlidingRemoveView;
        private View mContainerView;
        private View mDeleteButton;
        private TextView mPersonNameText;
        private TextView mPersonIDCardText;
        private TextView mPersonOtherInfoText;
        private int mPosition;

        public ViewHolder(View view) {
            mSlidingRemoveView = (SlidingRemoveView) view;
            mContainerView = view.findViewById(R.id.container_view);
            mDeleteButton = view.findViewById(R.id.btn_delete);
            mPersonNameText = (TextView) view.findViewById(R.id.person_name_text);
            mPersonIDCardText = (TextView) view.findViewById(R.id.person_id_card_text);
            mPersonOtherInfoText = (TextView) view.findViewById(R.id.person_other_info_text);
        }

    }

    public interface OnItemRemoveListener {
        void onRemove(BaseAdapter adapter, int position);
    }

    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        mOnItemRemoveListener = listener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
