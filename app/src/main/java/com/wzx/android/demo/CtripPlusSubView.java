package com.wzx.android.demo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

/**
 * Created by tychai on 14-5-4.
 */
public class CtripPlusSubView extends LinearLayout implements View.OnClickListener {
    private View plusBtn, subBtn; //加减号view
    private TextView numView; //数量view
    private int num = 0;
    private String unit = "";
    private int maxNum = 0;
    private int minNum = 0;
    private PlusSubCallBackListenre plusSubCallBackListener;

    public CtripPlusSubView(Context context) {
        this(context, null);
    }

    public CtripPlusSubView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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

    public void setPlusSubCallBackListener(PlusSubCallBackListenre plusSubCallBackListener) {
        this.plusSubCallBackListener = plusSubCallBackListener;
    }

    public interface PlusSubCallBackListenre {
        public void onPlusClick();

        public void onSubClick();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.plus_btn) {
            num++;
            refresh();
            plusSubCallBackListener.onPlusClick();

        } else if (i == R.id.sub_btn) {
            if (num > 0)
                num--;
            refresh();
            plusSubCallBackListener.onSubClick();

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

    public void setMaxNum(int num) {
        this.maxNum = num;
        refresh();
    }

    public void setMinNum(int num) {
        this.minNum = num;
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


    public void setNum(int num) {
        this.num = num;
        refresh();
    }

    public void setUnit(String unit) {
        this.unit = unit;
        refresh();
    }

    public void setNumViewBackgroud(int resId) {
        if (numView != null) {
            numView.setBackgroundResource(resId);
        }
    }


    public void setPlusBtnEnable(boolean enable) {
        plusBtn.setEnabled(enable);
    }

    public void setSubBtnEnable(boolean enable) {
        subBtn.setEnabled(enable);
    }
}
