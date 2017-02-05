package com.wzx.android.demo.text;

import android.app.Activity;
import android.graphics.Canvas;
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

/**
 * Created by wang_zx on 2015/12/17.
 */
public class HotelSpannableTextActivity extends Activity implements View.OnClickListener {


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
        setContentView(R.layout.activity_text);

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
