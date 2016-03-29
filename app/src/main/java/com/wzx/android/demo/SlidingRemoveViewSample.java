package com.wzx.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wzx.android.demo.recycleable.SlidingRemoveGridLayout;
import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2016/2/3.
 */
public class SlidingRemoveViewSample extends Activity implements View.OnClickListener {

    private SlidingRemoveGridLayout mSlidingRemoveGridLayout;
    private InsuredPersonAdapter mPersonAdapter;
    private SlidingRemoveView mSlideRemoveView;
    private View mOpenButton;
    private View mResetButton;

    private LinearLayout mContainer;
    private View mView;
    private HotelPlusSubViewHolder mSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_delete);

        mSlidingRemoveGridLayout = (SlidingRemoveGridLayout) findViewById(R.id.sliding_remove_grid);
        mPersonAdapter = new InsuredPersonAdapter(this);
        mSlidingRemoveGridLayout.setAdapter(mPersonAdapter);

        List<Object> insuranceTypeList = new ArrayList<Object>();
        for (int i = 0; i < 16; i++) {
            Object object = new Object();
            insuranceTypeList.add(object);
        }
        mPersonAdapter.setData(insuranceTypeList);

        mContainer = (LinearLayout) findViewById(R.id.container_layout);
        mView = getLayoutInflater().inflate(R.layout.plus_sub_test, mContainer, false);
        mContainer.addView(mView);

        mSelector = new HotelPlusSubViewHolder(mView);
        mSelector.setMinNum(1);
        mSelector.setMaxNum(4);
        mSelector.setNum(1);
        mSelector.setPlusSubCallBackListener(new HotelPlusSubViewHolder.PlusSubCallBackListener() {
            @Override
            public void onPlusClick() {
                Toast.makeText(SlidingRemoveViewSample.this, "" + mSelector.getNum(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubClick() {
                Toast.makeText(SlidingRemoveViewSample.this, "" + mSelector.getNum(), Toast.LENGTH_SHORT).show();
            }
        });

        mSlideRemoveView = (SlidingRemoveView) findViewById(R.id.slide_delete_view);
        mOpenButton = findViewById(R.id.btn_open);
        mOpenButton.setOnClickListener(this);
        mResetButton = findViewById(R.id.btn_reset);
        mResetButton.setOnClickListener(this);

        mSlideRemoveView.setOnViewRemoveListener(new SlidingRemoveView.OnViewRemoveListener() {
            @Override
            public void onRemoveFinish(SlidingRemoveView slidingRemoveView) {
                Toast.makeText(SlidingRemoveViewSample.this, "onRemoveFinish", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_open) {
            mSlideRemoveView.open();
        } else if (id == R.id.btn_reset) {
            //mSlideRemoveView.reset();
            mContainer.removeAllViews();
            mSelector.setMaxNum(16);
            mSelector.setNum(1);
            mContainer.addView(mView);

        }
    }
}
