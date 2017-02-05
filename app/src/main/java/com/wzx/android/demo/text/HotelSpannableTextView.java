package com.wzx.android.demo.text;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2016/1/6.
 */
public class HotelSpannableTextView extends View {

    private HotelSingleTextLayoutMaker mTextLayoutMaker = HotelSingleTextLayoutMaker.getInstance();

    private ArrayList<TextEntity> mTextEntities = new ArrayList<TextEntity>();
    private ArrayList<Layout> mTextLayouts = new ArrayList<Layout>();

    private boolean mStrikeThroughText = true;
    private int mTextDescent = 0;

    public HotelSpannableTextView(Context context) {
        this(context, null);
    }

    public HotelSpannableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelSpannableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void buildTextView(Builder builder) {
        mTextEntities.clear();
        mTextEntities.addAll(builder.mTextList);

        mTextLayouts.clear();
        mTextDescent = 0;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!mTextEntities.isEmpty() && mTextLayouts.isEmpty()) {
            for (TextEntity entity : mTextEntities) {
                Layout textLayout = mTextLayoutMaker.makeTextLayout(getContext(), entity.text, entity.appearance, mStrikeThroughText, entity.cacheLayout);
                mTextLayouts.add(textLayout);
            }
        }
        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();
        int width = hPadding;
        int height = 0;
        mTextDescent = 0;
        for (Layout textLayout : mTextLayouts) {
            width += textLayout.getLineWidth(0);
            height = Math.max(height, textLayout.getHeight());
            mTextDescent = Math.max(mTextDescent, textLayout.getLineDescent(0));
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }
        height += vPadding;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int height = getHeight() - getPaddingBottom() - mTextDescent;
        for (Layout textLayout : mTextLayouts) {
            canvas.save();
            canvas.translate(left, height - textLayout.getHeight() + textLayout.getLineDescent(0));
            textLayout.draw(canvas);
            canvas.restore();

            left += textLayout.getLineWidth(0);
        }
    }

    public static class Builder {
        List<TextEntity> mTextList = new ArrayList<TextEntity>();

        public Builder append(String text, int appearance) {
            return append(text, appearance, true);
        }

        public Builder append(String text, int appearance, boolean cacheLayout) {
            TextEntity entity = new TextEntity();
            entity.text = text;
            entity.appearance = appearance;
            entity.cacheLayout = cacheLayout;
            mTextList.add(entity);

            return this;
        }

        public void into(HotelSpannableTextView textView) {
            textView.buildTextView(this);
        }

    }

    private static class TextEntity {
        String text;
        int appearance;
        boolean cacheLayout;
    }
}
