package com.wzx.android.demo;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;

import ctrip.android.hotel.filter.DeviceInfoUtil;

/**
 * Created by wangzhenxing on 16/8/2.
 */
public class HotelGoodsRecommendDrawable extends Drawable {

    private Paint mTextPaint = new Paint();
    private String mText;

    public HotelGoodsRecommendDrawable() {
        mTextPaint.setColor(0xffff0000);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setTextSize(DeviceInfoUtil.getPixelFromDip(20));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setText(String text) {
        mText = text;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        Path path = new Path();
        path.moveTo(0, width);
        path.lineTo(height, 0);
        canvas.drawColor(0xffffff00);

        mTextPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mTextPaint);

        mTextPaint.setStyle(Paint.Style.FILL);
        if (!TextUtils.isEmpty(mText)) {
            canvas.drawTextOnPath(mText, path, 0, 0 , mTextPaint);
        }
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
}
