package com.wzx.android.demo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class GlobalAnimationHelper {
	
	private static final String TAG = GlobalAnimationHelper.class.getSimpleName();
	
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, GlobalAnimationHelper> sMap = new HashMap<Integer, GlobalAnimationHelper>();

	private WeakReference<Activity> mActivityReference;
	private WeakReference<FrameLayout> mAnimContainerReference;
	private final int[] mCoordinatesTemp = new int[2];

	private GlobalAnimationHelper(Activity activity) {
		mActivityReference = new WeakReference<Activity>(activity);
	}

	public static GlobalAnimationHelper getInstance(Activity activity) {
		if (activity == null)
			return null;
		int code = activity.hashCode();
		if (!sMap.containsKey(code)) {
			GlobalAnimationHelper helper = new GlobalAnimationHelper(activity);
			sMap.put(code, helper);
		}

		return sMap.get(code);

	}
	
	@SuppressWarnings("deprecation")
	public void startAnimation(View v, Animation animation) {
		Activity activity = mActivityReference.get();
		if (activity != null) {
			Bitmap bitmap = BitmapHelper.getViewBitmap(v);
			v.getLocationOnScreen(mCoordinatesTemp);
			int screenX = mCoordinatesTemp[0];
			int screenY = mCoordinatesTemp[1];
			
			FrameLayout animContainerView = mAnimContainerReference == null ? null : mAnimContainerReference.get();
			if (animContainerView == null) {
				Window window = activity.getWindow();
				FrameLayout decorView = (FrameLayout) window.getDecorView();
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				animContainerView = new  FrameLayout(activity);
				decorView.addView(animContainerView, params);
				mAnimContainerReference =  new WeakReference<FrameLayout>(animContainerView);
			}			
			
			LayoutParams params = new LayoutParams(v.getWidth(), v.getHeight());
			params.leftMargin = screenX;
			params.topMargin = screenY;
			ImageView image = new ImageView(activity);
			image.setBackgroundDrawable(new BitmapDrawable(bitmap));
			animContainerView.addView(image, params);
			
			animation.setAnimationListener(new GlobalAnimationListener(image));			
			image.startAnimation(animation);
		}
		
	}
	
	private class GlobalAnimationListener implements AnimationListener {
		
		private View mView;
		
		public GlobalAnimationListener(View v) {
			mView = v;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			mView.setVisibility(View.GONE);
			mView.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ViewParent parent = mView.getParent();
					if (parent instanceof ViewGroup) {
						((ViewGroup)parent).removeView(mView);
					}
				}
			});
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
	}


}
