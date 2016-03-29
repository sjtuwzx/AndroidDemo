package com.wzx.android.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/12/2.
 */
public class HotelAutoScaleTextView extends View {

    public static final String PLACEHOLDER_AUTO_SCALE_TEXT = "{auto_scale}";

    private int mTextSize;
    private int mTextColor;
    private String mConstantText;
    private String mAutoScaleText;
    private int mMaxLine = Integer.MAX_VALUE;
    private int mMinAutoScaleChar = 1;

    private Layout mTextLayout;
    private TextPaint mTextPaint = new TextPaint();

    public HotelAutoScaleTextView(Context context) {
        this(context, null);
    }

    public HotelAutoScaleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelAutoScaleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HotelAutoScaleTextView, defStyleAttr, 0);
        mConstantText = a.getString(R.styleable.HotelAutoScaleTextView_constantText);
        mAutoScaleText = a.getString(R.styleable.HotelAutoScaleTextView_autoText);
        mTextSize = a.getDimensionPixelSize(R.styleable.HotelAutoScaleTextView_textSize, 0);
        mTextColor = a.getColor(R.styleable.HotelAutoScaleTextView_textColor, 0);
        mMaxLine = a.getInt(R.styleable.HotelAutoScaleTextView_max_line, Integer.MAX_VALUE);
        mMinAutoScaleChar = a.getInt(R.styleable.HotelAutoScaleTextView_min_auto_scale_char, 1);

        a.recycle();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    public void setConstantText(String text) {
        if (mConstantText != text && (mConstantText == null || !mConstantText.equals(text))) {
            mConstantText = text;
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setAutoScaleText(String text) {
        if (mAutoScaleText != text && (mAutoScaleText == null || !mAutoScaleText.equals(text))) {
            mAutoScaleText = text;
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setTextSize(int size) {
        if (mTextSize != size) {
            mTextSize = size;
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setTextColor(int color) {
        if (mTextColor != color) {
            mTextColor = color;
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    private void nullLayouts() {
        mTextLayout = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();
        int width = MeasureSpec.getSize(widthMeasureSpec) - hPadding;
        if (mTextLayout == null && !TextUtils.isEmpty(mConstantText)) {
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(mTextColor);
            mTextLayout = new TextLayoutCreator(mMaxLine, mMinAutoScaleChar).createLayout(mConstantText, mAutoScaleText, mTextPaint, width);
        }
        int height = 0;
        if (mTextLayout != null) {
            height += mTextLayout.getHeight();
        }
        height += vPadding;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTextLayout != null) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            mTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    static class TextLayoutCreator {

        private int mMaxLines = Integer.MAX_VALUE;
        private int mMinAutoScaleChar = 1;

        public TextLayoutCreator(int maxLines, int minAutoScaleChar) {
            mMaxLines = maxLines;
            mMinAutoScaleChar = minAutoScaleChar;
        }

        public Layout createLayout(String constantText, final String autoScaleText, TextPaint paint, int width) {
            if (constantText.contains(PLACEHOLDER_AUTO_SCALE_TEXT)) {
                String tmpAutoScaleText = autoScaleText;
                while (tmpAutoScaleText.length() > mMinAutoScaleChar) {
                    String text = constantText.replace(PLACEHOLDER_AUTO_SCALE_TEXT, tmpAutoScaleText + (autoScaleText.equals(tmpAutoScaleText) ? "" : "..."));
                    Layout layout = new TextLayout(text, paint, width);
                    int lineCount = layout.getLineCount();
                    if (lineCount <= mMaxLines) {
                        return layout;
                    }
                    tmpAutoScaleText = tmpAutoScaleText.substring(0, tmpAutoScaleText.length() - 1);
                }
                String text = constantText.replace(PLACEHOLDER_AUTO_SCALE_TEXT, tmpAutoScaleText + (autoScaleText.equals(tmpAutoScaleText) ? "" : "..."));
                return new EllipsizeTextLayout(text, paint, width, mMaxLines);
            }
            return new EllipsizeTextLayout(constantText, paint, width, mMaxLines);

        }

        private static class TextLayout extends StaticLayout {

            public TextLayout(CharSequence text, TextPaint paint, int width) {
                super(text, 0, text.length(), paint, width, Alignment.ALIGN_NORMAL, 1.0f,
                        0.0f, false);
            }
        }

        private static class EllipsizeTextLayout extends DynamicLayout {

            private int mMaxLines = Integer.MAX_VALUE;

            public EllipsizeTextLayout(CharSequence text, TextPaint paint, int width, int maxLine) {
                super(text, text, paint, width, Alignment.ALIGN_NORMAL, 1.0f,
                        0.0f, false, TextUtils.TruncateAt.END, 0);
                mMaxLines = maxLine;
            }

            @Override
            public int getLineCount() {
                if (super.getLineCount() - 1 > mMaxLines) {
                    return mMaxLines;
                }
                return super.getLineCount() - 1;
            }

            @Override
            public int getEllipsisCount(int line) {
                if (line == mMaxLines - 1 && super.getLineCount() - 2 > line) {
                    return 1;
                }
                return 0;
            }

            @Override
            public int getEllipsisStart(int line) {
                if (line == mMaxLines - 1 && super.getLineCount() - 2 > line) {
                    return getLineEnd(line) - getLineStart(line) - 1;
                }
                return 0;
            }

        }
    }
}
