package com.wzx.android.demo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2016/2/17.
 */
public class HotelPlusSubView extends DividerLinearLayout implements View.OnClickListener {

    private View plusBtn, subBtn; //加减号view
    private TextView numView; //数量view
    private int num = 0;
    private String unit = "";
    private int maxNum = 0;
    private int minNum = 0;
    private PlusSubCallBackListener plusSubCallBackListener;

    public HotelPlusSubView(Context context) {
        this(context, null);
    }

    public HotelPlusSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelPlusSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        plusBtn = findViewById(R.id.plus_btn);
        subBtn = findViewById(R.id.sub_btn);
        numView = (TextView) findViewById(R.id.num_value);
        subBtn.setOnClickListener(this);
        plusBtn.setOnClickListener(this);

        refresh();
    }

    public void setPlusSubCallBackListener(PlusSubCallBackListener plusSubCallBackListener) {
        this.plusSubCallBackListener = plusSubCallBackListener;
    }

    public interface PlusSubCallBackListener {
        void onPlusClick();

        void onSubClick();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.plus_btn) {
            if (num < maxNum) {
                num++;
                refresh();
                plusSubCallBackListener.onPlusClick();
            }

        } else if (i == R.id.sub_btn) {
            if (num > minNum) {
                num--;
                refresh();
                plusSubCallBackListener.onSubClick();
            }

        }

    }

    private void refresh() {
        if (num <= getMinNum()) {
            subBtn.setEnabled(false);
        } else {
            subBtn.setEnabled(true);
        }
        if (getMaxNum() != 0 && num >= getMaxNum()) {
            plusBtn.setEnabled(false);
        } else {
            plusBtn.setEnabled(true);
        }
        if (TextUtils.isEmpty(unit)) {
            numView.setText(String.valueOf(num));
        } else {
            numView.setText(num + unit);
        }

    }

    public void setMinNum(int num) {
        this.minNum = num;
        refresh();
    }

    public void setMaxNum(int num) {
        this.maxNum = num;
        refresh();
    }

    public void setNum(int num) {
        this.num = num;
        refresh();
    }

    public void setUnit(String unit) {
        this.unit = unit;
        refresh();
    }

    private int getMaxNum() {
        return maxNum;
    }

    private int getMinNum() {
        return minNum;
    }

    public int getNum() {
        return num;
    }

}
