package com.wzx.label;

import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by wang_zx on 2015/12/23.
 */
public abstract class HotelLabelBaseDrawable extends Drawable {

    protected int mMeasuredWidth = 0;
    protected int mMeasuredHeight = 0;

    public abstract void measure(int widthMeasureSpec, int heightMeasureSpec);

    public void layout(int left, int top, int right, int bottom) {
        setBounds(left, top, right, bottom);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public final int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public final int getMeasuredHeight() {
        return mMeasuredHeight;
    }
}
