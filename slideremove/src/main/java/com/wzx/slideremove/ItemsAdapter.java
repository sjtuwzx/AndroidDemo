package com.wzx.slideremove;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ItemsAdapter extends ArrayAdapter<String> {
	
	private static final String TAG = ItemsAdapter.class.getSimpleName();

	private final LayoutInflater mInflater;

	public ItemsAdapter(Context context) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.mSlidingRemoveView = (SlidingRemoveView) convertView.findViewById(R.id.sliding_remove_view);
			holder.mSlidingRemoveView.setShowRemoveAnimator(true);
			holder.mText = (TextView) convertView.findViewById(R.id.text);
			holder.mButton = (Button) convertView.findViewById(R.id.btn);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mSlidingRemoveView.reset();
		holder.mSlidingRemoveView.setOnViewRemoveListener(new SlidingRemoveView.OnViewRemoveListener() {
			@Override
			public void onRemoveFinish(SlidingRemoveView slidingRemoveView) {
				remove(getItem(position));
			}
		});

		String title = getItem(position);
		holder.mText.setText(title);

		return convertView;
	}
	
	private static class ViewHolder {
		SlidingRemoveView mSlidingRemoveView;
		TextView mText;
		Button mButton;
		
	}

}