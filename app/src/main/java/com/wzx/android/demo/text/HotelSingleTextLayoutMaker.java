package com.wzx.android.demo.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.LruCache;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/12/25.
 */
public class HotelSingleTextLayoutMaker {

    private ThreadLocal<StringBuffer> mStringBuffers = new ThreadLocal<StringBuffer>() {
        @Override
        protected StringBuffer initialValue() {
            return new StringBuffer(16);
        }
    };

    private LruCache<String, Layout> mTextLayoutCache = new LruCache<String, Layout>(32);

    private LruCache<String, TextPaint> mTextPaintCache = new LruCache<String, TextPaint>(16);

    public static HotelSingleTextLayoutMaker getInstance() {
        return SingleInstanceHolder.sInstance;
    }

    private static class SingleInstanceHolder {
        private static final HotelSingleTextLayoutMaker sInstance = new HotelSingleTextLayoutMaker();

    }

    private HotelSingleTextLayoutMaker() {

    }

    public Layout makeTextLayout(Context context, String text, int appearance, boolean strikeThrough, boolean cacheLayout) {
        String key = createLayoutKey(text, appearance, strikeThrough);
        Layout textLayout = null;
        if (cacheLayout) {
            textLayout = mTextLayoutCache.get(key);
        }
        if (textLayout == null) {
            TextPaint paint = makeTextPaint(context, appearance, strikeThrough);
            textLayout = new TextLayout(text, paint, Integer.MAX_VALUE);
            if (cacheLayout) {
                mTextLayoutCache.put(key, textLayout);
            }
        }
        return textLayout;
    }

    private TextPaint makeTextPaint(Context context, int appearance, boolean strikeThrough) {
        String key = createPaintKey(appearance, strikeThrough);
        TextPaint paint = mTextPaintCache.get(key);
        if (paint == null) {
            TypedArray a = context.obtainStyledAttributes(appearance, R.styleable.TextAppearance2);
            int textSize = a.getDimensionPixelSize(R.styleable.TextAppearance2_android_textSize, 0);
            int textColor = a.getColor(R.styleable.TextAppearance2_android_textColor, Color.TRANSPARENT);
            int textStyle = a.getInt(R.styleable.TextAppearance2_android_textStyle, 0);
            a.recycle();

            paint = new TextPaint();
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            if (textStyle != 0) {
                Typeface tf = paint.getTypeface();
                int style = 0;

                if (tf != null) {
                    style = tf.getStyle();
                }

                style |= textStyle;

                if (tf == null) {
                    tf = Typeface.defaultFromStyle(style);
                } else {
                    tf = Typeface.create(tf, style);
                }

                int fake = style & ~tf.getStyle();

                if ((fake & Typeface.BOLD) != 0) {
                    paint.setFakeBoldText(true);
                }

                if ((fake & Typeface.ITALIC) != 0) {
                    paint.setTextSkewX(-0.25f);
                }

                paint.setTypeface(tf);
            }
            paint.setStrikeThruText(strikeThrough);

            mTextPaintCache.put(key, paint);
        }
        return paint;
    }

    private String createLayoutKey(String text, int appearance, boolean strikeThrough) {
        StringBuffer stringBuffer = fetchStringBuffer();
        stringBuffer.append(text).append("|").append(appearance).append("|").append(strikeThrough);

        return stringBuffer.toString();
    }

    private String createPaintKey(int appearance, boolean strikeThrough) {
        StringBuffer stringBuffer = fetchStringBuffer();
        stringBuffer.append(appearance).append("|").append(strikeThrough);

        return stringBuffer.toString();
    }

    private StringBuffer fetchStringBuffer() {
        StringBuffer sb = mStringBuffers.get();
        sb.setLength(0);

        return sb;
    }

    private static class TextLayout extends StaticLayout {

        public TextLayout(CharSequence text, TextPaint paint, int width) {
            super(text, 0, text.length(), paint, width, Alignment.ALIGN_NORMAL, 1.0f,
                    0.0f, true);
        }
    }
}
