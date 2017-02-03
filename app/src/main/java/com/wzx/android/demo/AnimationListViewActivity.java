package com.wzx.android.demo;

import java.util.ArrayList;
import java.util.List;

import com.wzx.android.demo.utils.FieldUtils;
import com.wzx.android.demo.v2.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

public class AnimationListViewActivity extends Activity{
	
	private AnimationListView mListView;
    private HotelOrderPostAdapter mAdapter;

    private View mOrderSuccessAnimView;
    private HotelOrderSuccessAnimDrawable mAnimDrawable;

    private View mOrderSuccessText;

    private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alpha_list);

        mOrderSuccessAnimView = findViewById(R.id.order_success_anim);
        mAnimDrawable = new HotelOrderSuccessAnimDrawable(0xFF099FDE, 3);
        mOrderSuccessAnimView.setBackgroundDrawable(mAnimDrawable);

        mOrderSuccessText = findViewById(R.id.order_success_text);


        SeekBar seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                int max = seekBar.getMax();
                float factor = (float) progress / max;
                mAnimDrawable.setFactor(factor);
            }
        });


		mListView = (AnimationListView)findViewById(R.id.list);
        mAdapter = new HotelOrderPostAdapter(this);
        mListView.setAdapter(mAdapter);

        mImageView = (ImageView)findViewById(R.id.image);

        findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               /* try {
                    snapshotPreActivity();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }*/
                mOrderSuccessText.setAlpha(0);

                ObjectAnimator tickAnimator = ObjectAnimator.ofFloat(mAnimDrawable, "factor", 0.0f, 1.0f);
                tickAnimator.setDuration(1100);

                ValueAnimator emptyAnimator = ValueAnimator.ofInt(0);
                emptyAnimator.setDuration(30);

                ObjectAnimator textAnimator = ObjectAnimator.ofFloat(mOrderSuccessText, "alpha", 0.0f, 1.0f);
                textAnimator.setDuration(100);
                textAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        int height = mOrderSuccessText.getHeight();
                        mOrderSuccessText.setPivotY(height);
                    }
                });

                ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(mOrderSuccessText, "rotationX", 0.0f, 90.0f);
                rotateAnimator.setDuration(500);

                ObjectAnimator rotateAnimator2 = ObjectAnimator.ofFloat(mOrderSuccessText, "rotationX", 90.0f, 0.0f);
                rotateAnimator2.setDuration(500);

                rotateAnimator2.setRepeatCount(Integer.MAX_VALUE);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(tickAnimator, emptyAnimator, textAnimator, rotateAnimator, rotateAnimator2);
                animatorSet.start();



               /* List<HotelStatusItemModel> data = new ArrayList<HotelStatusItemModel>();
                for (int i = 0; i < 30; i++) {
                    HotelStatusItemModel model = new HotelStatusItemModel();
                    model.description = "等待酒店确认，预计2015-01-21 15:10前短信通知您处理结果";
                    model.time = "2015-01-21 15:10";
                    data.add(model);
                }
                mAdapter.setData(data);
                mListView.startAnimation();*/
            }
        });
	}

    private void snapshotPreActivity() throws ClassNotFoundException, IllegalAccessException {
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Object windowManagerGlobal = FieldUtils.readField(windowManager, "mGlobal");
        ArrayList<View> views = (ArrayList<View>)FieldUtils.readField(windowManagerGlobal, "mViews");

        int N = views.size();
        Log.i("wzx", "views.size(): " + N);
        if (N > 1) {
            View view = views.get(N - 2);
            Bitmap bitmap = BitmapHelper.getViewBitmap(view);
            mImageView.setImageBitmap(bitmap);
        }
    }

}
