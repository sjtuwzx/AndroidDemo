package com.wzx.label;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HotelLabelView mLabelView;
    private HotelVerticalLabelView mVerticalLabelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLabelView = (HotelLabelView) findViewById(R.id.label_view);
        mVerticalLabelView = (HotelVerticalLabelView) findViewById(R.id.vertical_label_view);

        initLabelView();
        initVerticalLabelView();
    }

    private void initLabelView() {
        List<HotelLabelBaseDrawable> leftLabelDrawables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HotelLabelDrawable drawable = new HotelLabelDrawable();

            HotelLabelModel model = new HotelLabelModel();
            model.mFrameColor = 0xffffb786;
            model.mFrameWidth = 1.0f;
            model.mFrameCornerRadius = 1.0f;

            model.mMainText = "今夜特惠";
            model.mMainTextColor = 0xffff6000;
            model.mMainTextSize = 9.0f;
            model.mMainBackgroundColor = 0xffffffff;

            model.mSubText = "20RMB";
            model.mSubTextColor = 0xff00ff00;
            model.mSubTextSize = 10;
            model.mSubBackgroundColor = 0x80ff0000;

            drawable.setLabelModel(model);
            leftLabelDrawables.add(drawable);
        }
        mLabelView.refreshLeftLabelDrawables(leftLabelDrawables);

        List<HotelLabelBaseDrawable> rightLabelDrawables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HotelLabelDrawable drawable = new HotelLabelDrawable();

            HotelLabelModel model = new HotelLabelModel();
            model.mFrameColor = 0xffff0000;
            model.mFrameWidth = 1;
            model.mFrameCornerRadius = 5;

            model.mMainText = "首住特惠";
            model.mMainTextColor = 0xff00ff00;
            model.mMainTextSize = 10;
            model.mMainBackgroundColor = 0x80ff0000;

            model.mSubText = "20RMB";
            model.mSubTextColor = 0xffff6000;
            model.mSubTextSize = 8;
            model.mSubBackgroundColor = 0xfffff6f0;

            drawable.setLabelModel(model);
            rightLabelDrawables.add(drawable);
        }
        mLabelView.refreshRightLabelDrawables(rightLabelDrawables);


        List<HotelLabelBaseDrawable> priorityDisplayRightDrawables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            HotelLabelDrawable drawable = new HotelLabelDrawable();

            HotelLabelModel model = new HotelLabelModel();
            model.mFrameColor = Color.TRANSPARENT;
            model.mFrameWidth = 0;
            model.mFrameCornerRadius = 2.0f;

            model.mMainText = "超值返现";
            model.mMainTextColor = 0xffffffff;
            model.mMainTextSize = 11.0f;
            model.mMainBackgroundColor = 0xff4a81fb;

            drawable.setLabelModel(model);
            priorityDisplayRightDrawables.add(drawable);
        }
        mLabelView.refreshPriorityDisplayRightDrawables(priorityDisplayRightDrawables);
    }

    private void initVerticalLabelView() {
        List<HotelLabelBaseDrawable> labelDrawables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HotelLabelDrawable drawable = new HotelLabelDrawable();

            HotelLabelModel model = new HotelLabelModel();
            model.mFrameColor = 0xffffb786;
            model.mFrameWidth = 1.0f;
            model.mFrameCornerRadius = 1.0f;

            model.mMainText = "今夜特惠";
            model.mMainTextColor = 0xffff6000;
            model.mMainTextSize = 9.0f;
            model.mMainBackgroundColor = 0xffffffff;

            model.mSubText = "20RMB";
            model.mSubTextColor = 0xff00ff00;
            model.mSubTextSize = 10;
            model.mSubBackgroundColor = 0x80ff0000;

            drawable.setLabelModel(model);
            labelDrawables.add(drawable);
        }
        mVerticalLabelView.refreshLabelDrawables(labelDrawables);
    }
}
