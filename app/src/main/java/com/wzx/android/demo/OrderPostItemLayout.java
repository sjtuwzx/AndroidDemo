package com.wzx.android.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/5/28.
 */
public class OrderPostItemLayout extends LinearLayout implements AnimationListView.AnimationChild {

    private View mProgressRound;

    public OrderPostItemLayout(Context context) {
        this(context, null);
    }

    public OrderPostItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderPostItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mProgressRound = findViewById(R.id.progress_round);
    }

    @Override
    public void onAnimation(float factor) {
        setAlpha(factor);
        mProgressRound.setPivotX(mProgressRound.getWidth() / 2);
        mProgressRound.setPivotY(mProgressRound.getHeight() / 2);
        if (factor < 0.8f) {
            mProgressRound.setScaleX(0.2f + 1.25f * factor);
            mProgressRound.setScaleY(0.2f + 1.25f * factor);
        } else {
            mProgressRound.setScaleX(2f - factor);
            mProgressRound.setScaleY(2f - factor);
        }
    }
}
