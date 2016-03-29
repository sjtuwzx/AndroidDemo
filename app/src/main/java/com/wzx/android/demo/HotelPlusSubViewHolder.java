package com.wzx.android.demo;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2016/2/17.
 */
public class HotelPlusSubViewHolder implements View.OnClickListener{

    private View plusBtn, subBtn; //加减号view
    private TextView numView; //数量view
    private int num = 0;
    private String unit = "";
    private int maxNum = 0;
    private int minNum = 0;
    private PlusSubCallBackListener plusSubCallBackListener;

    public HotelPlusSubViewHolder(View view) {

        plusBtn = view.findViewById(R.id.plus_btn);
        subBtn = view.findViewById(R.id.sub_btn);
        numView = (TextView) view.findViewById(R.id.num_value);
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
        if (num <= minNum) {
            subBtn.setEnabled(false);
        } else {
            subBtn.setEnabled(true);
        }
        if (num >= maxNum) {
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

    public int getNum() {
        return num;
    }
}
