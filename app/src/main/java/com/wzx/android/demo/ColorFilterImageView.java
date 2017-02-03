package com.wzx.android.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wangzhenxing on 16/11/12.
 */

public class ColorFilterImageView extends ImageView {

    public ColorFilterImageView(Context context) {
        this(context, null);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs) {
        this(context, attrs, View.NO_ID);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorFilterImageView, defStyleAttr, 0);
        int filterColor = a.getColor(R.styleable.ColorFilterImageView_filter_color, Color.TRANSPARENT);
        if (filterColor != Color.TRANSPARENT) {
            setColorFilter(filterColor, PorterDuff.Mode.SRC_ATOP);
        }
        a.recycle();
    }
}
