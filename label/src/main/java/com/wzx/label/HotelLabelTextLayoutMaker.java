package com.wzx.label;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.LruCache;

/**
 * Created by wang_zx on 2015/12/25.
 */
public final class HotelLabelTextLayoutMaker {

    private static final StringBuilder STRING_BUILDER = new StringBuilder(32);

    private LruCache<String, Layout> mTextLayoutCache = new LruCache<String, Layout>(32);

    private LruCache<String, TextPaint> mTextPaintCache = new LruCache<String, TextPaint>(16);

    public static HotelLabelTextLayoutMaker getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }

    private static class SingleInstanceHolder{
        private static final HotelLabelTextLayoutMaker INSTANCE = new HotelLabelTextLayoutMaker();

    }

    private HotelLabelTextLayoutMaker() {

    }

    public Layout makeTextLayout(String text, float textSize, int textColor) {
        String key = createLayoutKey(text, textSize, textColor);
        Layout textLayout = mTextLayoutCache.get(key);
        if (textLayout == null) {
            TextPaint paint = makeTextPaint(textSize, textColor);
            textLayout = new TextLayout(text, paint, Integer.MAX_VALUE);

            mTextLayoutCache.put(key, textLayout);
        }
        return textLayout;
    }

    private TextPaint makeTextPaint(float textSize, int textColor) {
        String key = createPaintKey(textSize, textColor);
        TextPaint paint = mTextPaintCache.get(key);
        if (paint == null) {
            paint = new TextPaint();
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            paint.setColor(textColor);

            mTextPaintCache.put(key, paint);
        }
        return paint;
    }

    private String createLayoutKey(String text, float textSize, int textColor) {
        StringBuilder stringBuilder = fetchStringBuilder();
        stringBuilder.append(text).append("|").append(textSize).append("|").append(textColor);

        return stringBuilder.toString();
    }

    private String createPaintKey(float textSize, int textColor) {
        StringBuilder stringBuilder = fetchStringBuilder();
        stringBuilder.append(textSize).append("|").append(textColor);

        return stringBuilder.toString();
    }

    private StringBuilder fetchStringBuilder() {
        STRING_BUILDER.setLength(0);
        return STRING_BUILDER;
    }

    private static class TextLayout extends StaticLayout {

        private float mLineWidth;
        private int mHeight;
        private int mLineDescent;

        public TextLayout(CharSequence text, TextPaint paint, int width) {
            super(text, 0, text.length(), paint, width, Alignment.ALIGN_NORMAL, 1.0f,
                    0.0f, true);
            mLineWidth = super.getLineWidth(0);
            mHeight = super.getHeight();
            mLineDescent = super.getLineDescent(0);
        }

        @Override
        public float getLineWidth(int line) {
            return mLineWidth;
        }

        @Override
        public int getHeight() {
            return mHeight;
        }

        @Override
        public int getLineDescent(int line) {
            return mLineDescent;
        }
    }
}
