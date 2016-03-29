package com.wzx.android.demo;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by wang_zx on 2015/10/26.
 */
public class HotelBorderDrawable extends Drawable {

    private Paint mPaint = new Paint();

    public HotelBorderDrawable(int color) {
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        canvas.drawRect(1, 1, width - 1, height - 1, mPaint);

    }
}
