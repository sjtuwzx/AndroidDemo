package com.wzx.android.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/1/20.
 */
public class CollapsibleTextView extends View {

    private static final String TAG = CollapsibleTextView.class.getSimpleName();

    private CharSequence mText;

    private int mTextSize;

    private int mTextColor;

    private boolean mIsCollapsed = false;

    private int mMaxCollapseLine = 1;

    private String mExpandText;

    private String mCollapseText;

    private int mTipTextColor;

    private Drawable mExpandDrawable;

    private Drawable mCollapseDrawable;

    private Layout mTextLayout;

    private Layout mTipLayout;

    private TextPaint mTextPaint = new TextPaint();

    private TextPaint mTipPaint = new TextPaint();

    public CollapsibleTextView(Context context) {
        this(context, null);
    }

    public CollapsibleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollapsibleTextView, defStyle, 0);
        mText = a.getString(R.styleable.CollapsibleTextView_text);
        mTextSize = a.getDimensionPixelSize(R.styleable.CollapsibleTextView_textSize, 0);
        mTextColor = a.getColor(R.styleable.CollapsibleTextView_textColor, 0);
        mExpandText = a.getString(R.styleable.CollapsibleTextView_text_expand);
        mCollapseText = a.getString(R.styleable.CollapsibleTextView_text_collapse);
        mTipTextColor = a.getColor(R.styleable.CollapsibleTextView_tip_textColor, 0);
        mExpandDrawable = a.getDrawable(R.styleable.CollapsibleTextView_expand_button);
        mCollapseDrawable = a.getDrawable(R.styleable.CollapsibleTextView_collapse_button);
        mMaxCollapseLine = a.getInt(R.styleable.CollapsibleTextView_max_collapse_line, 1);
        mIsCollapsed = a.getBoolean(R.styleable.CollapsibleTextView_is_collapsed, false);
        if (mExpandDrawable != null) {
            mExpandDrawable.setBounds(0, 0, mExpandDrawable.getIntrinsicWidth(),
                    mExpandDrawable.getIntrinsicHeight());
        }
        if (mCollapseDrawable != null) {
            mCollapseDrawable.setBounds(0, 0, mCollapseDrawable.getIntrinsicWidth(),
                    mCollapseDrawable.getIntrinsicHeight());
        }
        a.recycle();
        mTextPaint.setAntiAlias(true);
        mTipPaint.setAntiAlias(true);
    }

    public void setText(CharSequence text) {
        if (mText != text && (mText == null || !mText.equals(text))) {
            mText = text;
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

    public void setCollapsed(boolean collapsed) {
        if (mIsCollapsed != collapsed) {
            mIsCollapsed = collapsed;
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public boolean isCollapsed() {
        return mIsCollapsed;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();
        int width = MeasureSpec.getSize(widthMeasureSpec) - hPadding;
        int height;
        if (mTextLayout == null) {
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(mTextColor);
            mTextLayout = new TextLayout(mText, mTextPaint, width);
            if (mTextLayout.getLineCount() <= mMaxCollapseLine) {
                height = mTextLayout.getHeight();

            } else {
                mTipPaint.setTextSize(mTextSize);
                mTipPaint.setColor(mTipTextColor);
                if (!mIsCollapsed) {
                    mTipLayout = new TextLayout(mCollapseText, mTipPaint, width);
                } else {
                    mTipLayout = new TextLayout(mExpandText, mTipPaint, width);
                    float paddingEnd = mTipLayout.getLineRight(0) + getTipDrawableWidth();
                    mTextLayout = new CollapseTextLayoutCreator(mMaxCollapseLine).createLayout(mText, mTextPaint,
                            width, paddingEnd);
                }
                height = mTextLayout.getHeight();
                if (width
                        - mTextLayout
                        .getLineRight(mTextLayout.getLineCount() - 1) < mTipLayout
                        .getLineRight(0) + getTipDrawableWidth()) {
                    height += mTipLayout.getHeight();
                }
            }
        } else {
            height = mTextLayout.getHeight();
            if (mTipLayout != null && width
                    - mTextLayout
                    .getLineRight(mTextLayout.getLineCount() - 1) < mTipLayout
                    .getLineRight(0) + getTipDrawableWidth()) {
                height += mTipLayout.getHeight();
            }
        }
        height += vPadding;
        super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextLayout != null) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            mTextLayout.draw(canvas);
            if (mTipLayout != null) {
                int saveCount = canvas.save();
                if (getWidth()
                        - mTextLayout
                        .getLineRight(mTextLayout.getLineCount() - 1) < mTipLayout
                        .getLineRight(0) + getTipDrawableWidth()) {
                    canvas.translate(0, mTextLayout.getHeight());
                } else {
                    canvas.translate(mTextLayout.getLineRight(mTextLayout
                            .getLineCount() - 1), mTextLayout
                            .getLineTop(mTextLayout.getLineCount() - 1));
                }
                mTipLayout.draw(canvas);
                if (mIsCollapsed) {
                    drawTipDrawable(canvas, mExpandDrawable);
                } else {
                    drawTipDrawable(canvas, mCollapseDrawable);
                }

                canvas.restoreToCount(saveCount);
            }
            canvas.restore();
        }
    }

    private void nullLayouts() {
        mTextLayout = mTipLayout = null;
    }

    private int getTipDrawableWidth() {
        if (mIsCollapsed && mExpandDrawable != null) {
            return mExpandDrawable.getIntrinsicWidth();
        } else if (!mIsCollapsed && mCollapseDrawable != null) {
            return mCollapseDrawable.getIntrinsicWidth();
        }
        return 0;
    }

    private void drawTipDrawable(Canvas canvas, Drawable drawable) {
        if (drawable != null) {
            canvas.translate(
                    mTipLayout.getLineRight(0),
                    (mTipLayout.getHeight() - drawable.getIntrinsicHeight()) / 2);
            drawable.draw(canvas);
        }
    }

    static class TextLayout extends DynamicLayout {
        public TextLayout(CharSequence text, TextPaint paint, int width) {
            super(text, text, paint, width, Alignment.ALIGN_NORMAL, 1.0f, 0.0f,
                    false);
        }

    }

    static class CollapseTextLayoutCreator {

        private static final int NUM_MAX_ELLIPSIS_COUNT = 10;
        private int mMaxLines;

        private int mEllipsisCount;

        public CollapseTextLayoutCreator(int maxLines) {
            mMaxLines = maxLines;
        }

        public Layout createLayout(CharSequence text, TextPaint paint, int width,
                                   float paddingEnd) {
            mEllipsisCount = 0;
            Layout layout = null;
            while (mEllipsisCount < NUM_MAX_ELLIPSIS_COUNT && (layout == null
                    || (width - layout.getLineRight(layout.getLineCount() - 1) < paddingEnd))) {
                ++mEllipsisCount;
                layout = new CollapseTextLayout(text, paint, width);
            }

            return layout;
        }

        private class CollapseTextLayout extends DynamicLayout {

            public CollapseTextLayout(CharSequence text, TextPaint paint, int width) {
                super(text, text, paint, width, Alignment.ALIGN_NORMAL, 1.0f,
                        0.0f, false, TruncateAt.END, 0);
            }

            @Override
            public int getLineCount() {
                if (super.getLineCount() > mMaxLines) {
                    return mMaxLines;
                }
                return super.getLineCount();
            }

            @Override
            public int getEllipsisStart(int line) {
                if (super.getLineCount() > mMaxLines && line == mMaxLines - 1) {
                    return getLineEnd(line) - getLineStart(line) - mEllipsisCount;
                }
                return 0;
            }

            @Override
            public int getEllipsisCount(int line) {
                if (super.getLineCount() > mMaxLines && line == mMaxLines - 1) {
                    return mEllipsisCount;
                }
                return 0;
            }

        }
    }

    static class SavedState extends BaseSavedState {
        CharSequence text;
        int textSize;
        int textColor;
        boolean isCollapsed;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            text =  TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            textSize = in.readInt();
            textColor = in.readInt();
            isCollapsed = (in.readInt() != 0);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            TextUtils.writeToParcel(text, out, flags);
            out.writeInt(textSize);
            out.writeInt(textColor);
            out.writeInt(isCollapsed ? 1 : 0);

        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.text = mText;
        ss.textSize = mTextSize;
        ss.textColor = mTextColor;
        ss.isCollapsed = mIsCollapsed;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mText = ss.text;
        mTextSize = ss.textSize;
        mTextColor = ss.textColor;
        mIsCollapsed = ss.isCollapsed;
    }

}
