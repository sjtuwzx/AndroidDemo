package com.wzx.label;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;

/**
 * Created by wang_zx on 2015/12/17.
 */
public class HotelLabelDrawable extends HotelLabelBaseDrawable {

    private static final float DEFAULT_FONT_SIZE = 10f;

    private Paint mFramePaint = new Paint();
    private Paint mMainBackgroundPaint = new Paint();
    private Paint mSubBackgroundPaint = new Paint();

    private HotelLabelTextLayoutMaker mTextLayoutMaker = HotelLabelTextLayoutMaker.getInstance();
    private Layout mMainTextLayout;
    private Layout mSubTextLayout;

    private HotelLabelModel mLabelModel;

    private float mLabelRadius = 0;

    private int mTextHorizontalPadding = 0;
    private int mTextVerticalPadding = 0;

    private RectF mFrameRect = new RectF();
    private Path mFramePath = new Path();

    public HotelLabelDrawable() {
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setAntiAlias(true);

        mMainBackgroundPaint.setAntiAlias(true);
        mSubBackgroundPaint.setAntiAlias(true);

        mTextHorizontalPadding = ViewUtils.dip2px(ApplicationHelper.getInstance().getApplication(), 2.5f);
    }

    public void setLabelModel(HotelLabelModel model) {
        mLabelModel = model;
        nullLayouts();
        invalidateSelf();
    }

    private void nullLayouts() {
        mMainTextLayout = null;
        mSubTextLayout = null;
    }

    private void buildTextLayout() {

        mFramePaint.setColor(mLabelModel.mFrameColor);
        int frameWidth = Math.max(0, ViewUtils.dip2px(ApplicationHelper.getInstance().getApplication(), mLabelModel.mFrameWidth));
        mFramePaint.setStrokeWidth(frameWidth);
        mLabelRadius = Math.max(0, ViewUtils.dip2px(ApplicationHelper.getInstance().getApplication(), mLabelModel.mFrameCornerRadius));

        if (!TextUtils.isEmpty(mLabelModel.mMainText)) {
            mMainBackgroundPaint.setColor(mLabelModel.mMainBackgroundColor);

            int textSize = getFontSize(mLabelModel.mMainTextSize, DEFAULT_FONT_SIZE);
            mMainTextLayout = mTextLayoutMaker.makeTextLayout(mLabelModel.mMainText, textSize, mLabelModel.mMainTextColor);
        }

        if (!TextUtils.isEmpty(mLabelModel.mSubText)) {
            mSubBackgroundPaint.setColor(mLabelModel.mSubBackgroundColor);

            int textSize = getFontSize(mLabelModel.mSubTextSize, DEFAULT_FONT_SIZE);
            mSubTextLayout = mTextLayoutMaker.makeTextLayout(mLabelModel.mSubText, textSize, mLabelModel.mSubTextColor);
        }
    }

    private static int getFontSize(float fontSize, float defaultFontSize) {
        if (fontSize > 0) {
            return ViewUtils.dip2px(ApplicationHelper.getInstance().getApplication(), fontSize);
        } else {
            return ViewUtils.dip2px(ApplicationHelper.getInstance().getApplication(), defaultFontSize);
        }
    }

    @Override
    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMainTextLayout == null && mSubTextLayout == null && mLabelModel != null) {
            buildTextLayout();
        }

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

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == View.MeasureSpec.EXACTLY) {
            mMeasuredWidth = widthSize;
        }
        if (heightMode == View.MeasureSpec.EXACTLY) {
            mMeasuredHeight = heightSize;
        }
    }

    private float getSingleLabelWidth(Layout layout) {
        return layout.getLineWidth(0) + mTextHorizontalPadding * 2;
    }

    private int getSingleLabelHeight(Layout layout) {
        return layout.getHeight() + mTextVerticalPadding * 2;
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        super.layout(left, top, right, bottom);

        mFrameRect.set(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        Rect bounds = getBounds();
        mFramePath.reset();
        mFramePath.addRoundRect(mFrameRect, mLabelRadius, mLabelRadius, Path.Direction.CW);
        canvas.clipPath(mFramePath);
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
            if (textLayout == mMainTextLayout && mSubTextLayout == null) {
                labelWidth = bounds.width();
            }
            drawSingleLabelBackground(canvas, backgroundPaint, labelWidth);

            canvas.save();
            float dx = (labelWidth - textLayout.getLineWidth(0)) / 2;
            float dy = (height - textLayout.getHeight() + textLayout.getTopPadding() + textLayout.getBottomPadding()) / 2.0f;
            canvas.translate(dx, dy);
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
        canvas.drawRect(rectF, paint);
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

}
