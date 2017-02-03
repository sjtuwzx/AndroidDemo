package com.wzx.android.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.wzx.android.demo.utils.DrawableUtil;
import com.wzx.android.demo.v2.R;

/**
 * Created by wangzhenxing on 16/11/14.
 */

public class TintDrawableTextView extends TextView {

    private static final int CONST_INT_3 = 3;

    private int mDrawableTintColor = Color.TRANSPARENT;

    public TintDrawableTextView(Context context) {
        this(context, null);
    }

    public TintDrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, View.NO_ID);
    }

    public TintDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintDrawableTextView, defStyleAttr, 0);
        mDrawableTintColor = a.getColor(R.styleable.TintDrawableTextView_drawable_tint_color, Color.TRANSPARENT);
        if (mDrawableTintColor != Color.TRANSPARENT) {
            Drawable[] drawables = getCompoundDrawables();
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[CONST_INT_3]);
        }
        a.recycle();
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (mDrawableTintColor == Color.TRANSPARENT) {
            super.setCompoundDrawables(left, top, right, bottom);
        } else {
            super.setCompoundDrawables(DrawableUtil.getTintDrawable(left, mDrawableTintColor),
                    DrawableUtil.getTintDrawable(top, mDrawableTintColor),
                    DrawableUtil.getTintDrawable(right, mDrawableTintColor),
                    DrawableUtil.getTintDrawable(bottom, mDrawableTintColor));
        }
    }
}
