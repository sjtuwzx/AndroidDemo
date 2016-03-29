package com.wzx.android.demo.label;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import ctrip.android.hotel.filter.DeviceInfoUtil;

/**
 * Created by wang_zx on 2015/12/17.
 */
public class HotelLabelDrawable extends Drawable {

    private final static float DEFAULT_FONT_SIZE = 10f;

    private Paint mFramePaint = new Paint();
    private TextPaint mMainTextPaint = new TextPaint();
    private TextPaint mSubTextPaint = new TextPaint();
    private Paint mMainBackgroundPaint = new Paint();
    private Paint mSubBackgroundPaint = new Paint();

    private Layout mMainTextLayout;
    private Layout mSubTextLayout;

    private float mLabelRadius = 0;

    private int mTextHorizontalPadding = 0;
    private int mTextVerticalPadding = 0;

    private int mMeasuredWidth = 0;
    private int mMeasuredHeight = 0;

    public HotelLabelDrawable() {
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setAntiAlias(true);

        mMainBackgroundPaint.setAntiAlias(true);
        mMainTextPaint.setAntiAlias(true);

        mSubBackgroundPaint.setAntiAlias(true);
        mSubTextPaint.setAntiAlias(true);

        mTextHorizontalPadding = DeviceInfoUtil.getPixelFromDip(2.5f);
    }

    public void setLabelModel(HotelTagViewModel model) {
        HotelTagStyleViewModel styleViewModel = model.styleViewModel;

        mFramePaint.setColor(parseColor(styleViewModel.tagFrameColor, Color.TRANSPARENT));
        int frameWidth = Math.max(0, DeviceInfoUtil.getPixelFromDip(styleViewModel.tagFrameWidth));
        mFramePaint.setStrokeWidth(frameWidth);
        mLabelRadius = Math.max(0, DeviceInfoUtil.getPixelFromDip(styleViewModel.tagCornerRadius));

        HotelTagBasicViewModel mainLabelModel = styleViewModel.mainTagViewModel;
        if (!TextUtils.isEmpty(mainLabelModel.tagTitle)) {
            mMainBackgroundPaint.setColor(parseColor(mainLabelModel.tagBackgroundColor, Color.WHITE));
            mMainTextPaint.setColor(parseColor(mainLabelModel.tagFontColor, Color.BLACK));
            mMainTextPaint.setTextSize(getFontSize(mainLabelModel.tagFontSize, DEFAULT_FONT_SIZE));
            mMainTextLayout = new TextLayout(mainLabelModel.tagTitle, mMainTextPaint, Integer.MAX_VALUE);

            printTextLayoutInfo(mMainTextLayout);
        }

        HotelTagBasicViewModel subLabelModel = styleViewModel.subTagViewModel;
        if (model.hasSubTitle && !TextUtils.isEmpty(subLabelModel.tagTitle)) {
            mSubBackgroundPaint.setColor(parseColor(subLabelModel.tagBackgroundColor, Color.WHITE));
            mSubTextPaint.setColor(parseColor(subLabelModel.tagFontColor, Color.BLACK));
            mSubTextPaint.setTextSize(getFontSize(subLabelModel.tagFontSize, DEFAULT_FONT_SIZE));
            mSubTextLayout = new TextLayout(subLabelModel.tagTitle, mSubTextPaint, Integer.MAX_VALUE);
        }

        invalidateSelf();
    }

    private void printTextLayoutInfo(Layout layout) {
        StringBuffer info = new StringBuffer();
        info.append("width: ").append(layout.getLineWidth(0)).append("\r\n")
                .append("height: ").append(layout.getHeight()).append("\r\n")
                .append("lineTop: ").append(layout.getLineTop(0)).append("\r\n")
                .append("lineBottom: ").append(layout.getLineBottom(0)).append("\r\n")
                .append("ascent: ").append(layout.getLineAscent(0)).append("\r\n")
                .append("descent: ").append(layout.getLineDescent(0)).append("\r\n")
                .append("baseline: ").append(layout.getLineBaseline(0)).append("\r\n")
                .append("topPadding: ").append(layout.getTopPadding()).append("\r\n")
                .append("bottomPadding: ").append(layout.getBottomPadding()).append("\r\n")
                .append("").append(layout.getSpacingAdd());

        Log.i("wzx", info.toString());
    }

    private static int parseColor(String color, int defaultColor) {
        try {
            return Color.parseColor(color);
        } catch (Exception e) {
            return defaultColor;
        }
    }

    private static int getFontSize(float fontSize, float defaultFontSize) {
        if (fontSize > 0) {
            return DeviceInfoUtil.getPixelFromDip(fontSize);
        } else {
            return DeviceInfoUtil.getPixelFromDip(defaultFontSize);
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

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasuredWidth = 0;
        mMeasuredHeight = 0;
        if (mMainTextLayout != null) {
            mMeasuredWidth += getSingleLabelWidth(mMainTextLayout);
            mMeasuredHeight = Math.max(mMeasuredHeight, getSingleLabelHeight(mMainTextLayout));
        }
        if (mSubTextLayout != null) {
            mMeasuredWidth += getSingleLabelWidth(mSubTextLayout);
            mMeasuredHeight = Math.max(mMeasuredHeight, getSingleLabelHeight(mSubTextLayout));
        }
    }

    public void layout(int left, int top, int right, int bottom) {
        setBounds(left, top, right, bottom);
    }

    private float getSingleLabelWidth(Layout layout) {
        return layout.getLineWidth(0) +  mTextHorizontalPadding * 2;
    }

    private int getSingleLabelHeight(Layout layout) {
        return layout.getHeight() + mTextVerticalPadding * 2;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        Rect bounds = getBounds();
        canvas.translate(bounds.left, bounds.top);

        float mainLabelWidth = drawSingleLabel(canvas, mMainTextLayout, mMainBackgroundPaint);
        canvas.save();
        canvas.translate(mainLabelWidth, 0);
        drawSingleLabel(canvas, mSubTextLayout, mSubBackgroundPaint);
        canvas.restore();
        drawFrame(canvas, mMainTextLayout != null && mSubTextLayout != null, mainLabelWidth);

        canvas.restore();
    }

    private float drawSingleLabel(Canvas canvas, Layout textLayout, Paint backgroundPaint) {
        if (textLayout != null) {
            Rect bounds = getBounds();
            int height = bounds.height();

            float labelWidth = getSingleLabelWidth(textLayout);
            drawSingleLabelBackground(canvas, backgroundPaint, labelWidth);

            canvas.save();

            canvas.translate((labelWidth - textLayout.getLineWidth(0)) / 2, (height - textLayout.getHeight() + textLayout.getTopPadding() + textLayout.getBottomPadding()) / 2);
            textLayout.draw(canvas);
            canvas.restore();

            return labelWidth;
        }
        return 0;
    }

    private void drawSingleLabelBackground(Canvas canvas, Paint paint, float with) {
        Rect bounds = getBounds();
        int height = bounds.height();
        float halfStrokeWidth = mFramePaint.getStrokeWidth() / 2;
        RectF rectF = new RectF(halfStrokeWidth, halfStrokeWidth, with - halfStrokeWidth, height - halfStrokeWidth);

        canvas.drawRoundRect(rectF, mLabelRadius, mLabelRadius, paint);
    }

    private void drawFrame(Canvas canvas, boolean drawDivider, float dividerX) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        float halfStrokeWidth = mFramePaint.getStrokeWidth() / 2;

        RectF rectF = new RectF(halfStrokeWidth, halfStrokeWidth, width - halfStrokeWidth, height - halfStrokeWidth);
        canvas.drawRoundRect(rectF, mLabelRadius, mLabelRadius, mFramePaint);

        if (drawDivider && dividerX > 0) {
            canvas.drawLine(dividerX, 0, dividerX, height, mFramePaint);
        }
    }

    private static class TextLayout extends StaticLayout {

        public TextLayout(CharSequence text, TextPaint paint, int width) {
            super(text, 0, text.length(), paint, width, Alignment.ALIGN_NORMAL, 1.0f,
                    0.0f, true);
        }
    }
}
