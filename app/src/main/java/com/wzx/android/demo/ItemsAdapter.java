package com.wzx.android.demo;

import java.util.List;

import com.wzx.android.demo.slidingremove.SlidingRemoveView;
import com.wzx.android.demo.v2.R;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemsAdapter extends ArrayAdapter<String> implements OnClickListener {
	
	private static final String TAG = ItemsAdapter.class.getSimpleName();

	private final LayoutInflater mInflater;

	private RecyclerView.RecycledViewPool mRecycledViewPool = new RecyclerView.RecycledViewPool();

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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.mSlidingRemoveView = (SlidingRemoveView) convertView.findViewById(R.id.sliding_remove_view);
			holder.mText = (TextView) convertView.findViewById(R.id.text);
			holder.mButton = (Button) convertView.findViewById(R.id.btn);

			RecyclerView recyclerView = (RecyclerView) convertView.findViewById(R.id.recycler_view);
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
			linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
			recyclerView.setLayoutManager(linearLayoutManager);
			recyclerView.setRecycledViewPool(mRecycledViewPool);
			recyclerView.setHasFixedSize(true);
			RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration();
			recyclerView.addItemDecoration(itemDecoration);

			holder.mRecyclerView = recyclerView;

			holder.mRecyclerAdapter = new ImageAdapter(getContext());
			holder.mRecyclerView.setAdapter(holder.mRecyclerAdapter);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mSlidingRemoveView.reset();
		holder.mSlidingRemoveView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), "mSlidingRemoveView", Toast.LENGTH_SHORT).show();
			}
		});
		holder.mText.setText("");

		String title = getItem(position);
		if (TextUtils.isEmpty(title)) {
			return convertView;
		}
		holder.mText.setText(title);
		holder.mButton.setOnClickListener(this);
		convertView.setAlpha(1.0f);

		holder.mRecyclerView.scrollToPosition(0);
		return convertView;
	}
	
	private static class ViewHolder {
		SlidingRemoveView mSlidingRemoveView;
		TextView mText;
		Button mButton;
		RecyclerView mRecyclerView;
		RecyclerView.Adapter mRecyclerAdapter;
		
	}

	private static class DividerItemDecoration extends  RecyclerView.ItemDecoration {

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			outRect.set(0, 0, 100, 0);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.applaud_animation);
		GlobalAnimationHelper.getInstance((Activity) v.getContext()).startAnimation(v, animation);
	}

	private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

		private final LayoutInflater mInflater;

		public ImageAdapter(Context context) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			Log.i("wzx", "onCreateViewHolder");

			View view = mInflater.inflate(R.layout.image_item, viewGroup, false);

			ViewHolder holder = new ViewHolder(view);
			return holder;
		}

		@Override
		public void onBindViewHolder(ViewHolder viewHolder, int i) {

		}

		@Override
		public int getItemCount() {
			return 10;
		}

		public static class ViewHolder extends RecyclerView.ViewHolder {

			public ImageView mImageView;

			public ViewHolder(View itemView) {
				super(itemView);
				mImageView = (ImageView)itemView;
			}
		}
	}

}