package com.wzx.android.demo.label;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wzx.android.demo.ChildrenLayoutObservableListView;
import com.wzx.android.demo.HotelGoodsRecommendDrawable;
import com.wzx.android.demo.pinnedHeader.ItemsAdapter;
import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

import ctrip.android.hotel.filter.DeviceInfoUtil;

/**
 * Created by wang_zx on 2015/12/17.
 */
public class HotelLabelActivity extends Activity implements View.OnClickListener {

    private HotelLabelView mLabelView;
    private HotelVerticalLabelView mVerticalLabelView;

    private TextView mText1;
    private TextView mText2;
    private TextView mText3;

    private Button mButton1;
    private Button mButton2;

    private ChildrenLayoutObservableListView mListView;
    private ItemsAdapter mAdapter;

    private HotelSpannableTextView mSpannableTextView;

    private View mRecommendLabelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        mListView = (ChildrenLayoutObservableListView) findViewById(R.id.list);
        mListView.setOnChildrenLayoutListener(new ChildrenLayoutObservableListView.OnChildrenLayoutListener() {
            @Override
            public void onChildrenLayout(ListView listView) {
                int N = listView.getChildCount();
                Log.i("wzx", "listView.getChildCount: " + N);
            }
        });
        mAdapter = new ItemsAdapter(this);
        List<String> data = new ArrayList<String>();
        int N =  30;
        for (int i = 0; i < N; i++) {
            data.add(String.format("%d - %d", 1, i + 1));
        }
        mAdapter.setData(data);
        mListView.setAdapter(mAdapter);

        mText1 = (TextView) findViewById(R.id.text1);
        mText2 = (TextView) findViewById(R.id.text2);
        mText3 = (TextView) findViewById(R.id.text3);
        mButton1 = (Button) findViewById(R.id.btn1);
        mButton2 = (Button) findViewById(R.id.btn2);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);


        mLabelView = (HotelLabelView) findViewById(R.id.label_view);

        mVerticalLabelView = (HotelVerticalLabelView) findViewById(R.id.vertical_label_view);

        List<HotelLabelDrawable> leftLabelDrawables = new ArrayList<HotelLabelDrawable>();
        for (int i = 0; i < 10; i++) {
            HotelLabelDrawable drawable = new HotelLabelDrawable();
            HotelTagViewModel model = new HotelTagViewModel();
            model.hasSubTitle = false;

            HotelTagStyleViewModel styleViewModel = new HotelTagStyleViewModel();
            styleViewModel.tagFrameColor = "#ffb786";
            styleViewModel.tagFrameWidth = 1.0f;
            styleViewModel.tagCornerRadius = 1.0f;
            model.styleViewModel = styleViewModel;

            HotelTagBasicViewModel mainLabelModel = new HotelTagBasicViewModel();
            mainLabelModel.tagTitle = "今夜特惠";
            mainLabelModel.tagFontColor = "#ff6000";
            mainLabelModel.tagFontSize = 9.0f;
            mainLabelModel.tagBackgroundColor = "#ffffff";
            styleViewModel.mainTagViewModel = mainLabelModel;

            HotelTagBasicViewModel subLabelModel = new HotelTagBasicViewModel();
            subLabelModel.tagTitle = "20RMB";
            subLabelModel.tagFontColor = "#ff00ff00";
            subLabelModel.tagFontSize = 10;
            subLabelModel.tagBackgroundColor = "#80ff0000";
            styleViewModel.subTagViewModel = subLabelModel;


            drawable.setLabelModel(model);
            leftLabelDrawables.add(drawable);
        }

        mVerticalLabelView.refreshLabelDrawables(leftLabelDrawables);

        List<HotelLabelDrawable> rightLabelDrawables = new ArrayList<HotelLabelDrawable>();
        for (int i = 0; i < 10; i++) {
            HotelLabelDrawable drawable = new HotelLabelDrawable();
            HotelTagViewModel model = new HotelTagViewModel();
            model.hasSubTitle = true;

            HotelTagStyleViewModel styleViewModel = new HotelTagStyleViewModel();
            styleViewModel.tagFrameColor = "#ffff0000";
            styleViewModel.tagFrameWidth = 1;
            styleViewModel.tagCornerRadius = 5;
            model.styleViewModel = styleViewModel;

            HotelTagBasicViewModel mainLabelModel = new HotelTagBasicViewModel();
            mainLabelModel.tagTitle = "首住特惠";
            mainLabelModel.tagFontColor = "#ff00ff00";
            mainLabelModel.tagFontSize = 10;
            mainLabelModel.tagBackgroundColor = "#80ff0000";
            styleViewModel.mainTagViewModel = mainLabelModel;

            HotelTagBasicViewModel subLabelModel = new HotelTagBasicViewModel();
            subLabelModel.tagTitle = "20RMB";
            subLabelModel.tagFontColor = "#ff6000";
            subLabelModel.tagFontSize = 8;
            subLabelModel.tagBackgroundColor = "#fff6f0";
            styleViewModel.subTagViewModel = subLabelModel;


            drawable.setLabelModel(model);
            rightLabelDrawables.add(drawable);
        }
        mLabelView.refreshRightLabelDrawables(rightLabelDrawables);


        List<HotelLabelDrawable> priorityDisplayRightDrawables = new ArrayList<HotelLabelDrawable>();
        for (int i = 0; i < 2; i++) {
            HotelLabelDrawable drawable = new HotelLabelDrawable();
            HotelTagViewModel model = new HotelTagViewModel();
            model.hasSubTitle = false;

            HotelTagStyleViewModel styleViewModel = new HotelTagStyleViewModel();
            styleViewModel.tagFrameColor = "";
            styleViewModel.tagFrameWidth = -1.0f;
            styleViewModel.tagCornerRadius = 2.0f;
            model.styleViewModel = styleViewModel;

            HotelTagBasicViewModel mainLabelModel = new HotelTagBasicViewModel();
            mainLabelModel.tagTitle = "超值返现";
            mainLabelModel.tagFontColor = "#ffffff";
            mainLabelModel.tagFontSize = 11.0f;
            mainLabelModel.tagBackgroundColor = "#4a81fb";
            styleViewModel.mainTagViewModel = mainLabelModel;

            HotelTagBasicViewModel subLabelModel = new HotelTagBasicViewModel();
            subLabelModel.tagTitle = "123";
            subLabelModel.tagFontColor = "#00000000";
            subLabelModel.tagFontSize = 11.0f;
            subLabelModel.tagBackgroundColor = "#00000000";
            styleViewModel.subTagViewModel = subLabelModel;

            model.hasSubTitle = true;


            drawable.setLabelModel(model);
            priorityDisplayRightDrawables.add(drawable);
        }
        mLabelView.refreshPriorityDisplayRightDrawables(priorityDisplayRightDrawables);


        mSpannableTextView = (HotelSpannableTextView) findViewById(R.id.spannable_text);

        new HotelSpannableTextView.Builder()
                .append("￥", R.style.text_14_ff9913)
                .append("256", R.style.text_18_ff9913_b)
                .into(mSpannableTextView);

        mRecommendLabelView = findViewById(R.id.recommand_label);
        HotelGoodsRecommendDrawable recommendDrawable = new HotelGoodsRecommendDrawable();
        recommendDrawable.setText("人气特卖");
        mRecommendLabelView.setBackgroundDrawable(recommendDrawable);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn1) {
            testSpannableText();
        } else if (id == R.id.btn2) {
            testNormalText();
        }
    }

    private void testSpannableText() {
        mAdapter.notifyDataSetChanged();
      /*  Canvas canvas = new Canvas();
        String avgCurrency = "$";
        String priceValue = "234";

        long startTimeMs = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            SpannableString ss = new SpannableString(avgCurrency + priceValue);
            ss.setSpan(new TextAppearanceSpan(this, R.style.text_10_bcbcbc_b), 0, avgCurrency.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new TextAppearanceSpan(this, R.style.text_15_ff9a14), avgCurrency.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mText1.setText(ss);
            mText1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mText1.layout(0, 0, mText1.getMeasuredWidth(), mText1.getMeasuredHeight());
            mText1.draw(canvas);
        }
        long delayMs = System.currentTimeMillis() - startTimeMs;
        Log.i("wzx", String.format("耗时:%ds", delayMs));
        Toast.makeText(this, String.format("耗时:%ds", delayMs), Toast.LENGTH_LONG).show();*/
    }

    private void testNormalText() {

        Canvas canvas = new Canvas();
        String avgCurrency = "$";
        String priceValue = "234";

        long startTimeMs = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            SpannableString ss = new SpannableString(avgCurrency + priceValue);
            ss.setSpan(new TextAppearanceSpan(this, R.style.text_10_bcbcbc_b), 0, avgCurrency.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new TextAppearanceSpan(this, R.style.text_15_ff9a14), avgCurrency.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mText2.setText(avgCurrency);
            mText2.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mText2.layout(0, 0, mText2.getMeasuredWidth(), mText2.getMeasuredHeight());
            mText2.draw(canvas);

            mText3.setText(priceValue);
            mText3.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mText3.layout(mText2.getMeasuredWidth(), 0, mText2.getMeasuredWidth() + mText3.getMeasuredWidth(), mText3.getMeasuredHeight());
            mText3.draw(canvas);
        }
        long delayMs = System.currentTimeMillis() - startTimeMs;
        Log.i("wzx", String.format("耗时:%ds", delayMs));
        Toast.makeText(this, String.format("耗时:%ds", delayMs), Toast.LENGTH_LONG).show();
    }
}
