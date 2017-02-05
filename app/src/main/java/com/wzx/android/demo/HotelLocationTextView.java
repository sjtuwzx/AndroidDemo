package com.wzx.android.demo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/8/20.
 */
public class HotelLocationTextView extends View {

    private static final String TAG = HotelLocationTextView.class.getSimpleName();

    private static final BoringLayout.Metrics UNKNOWN_BORING = new BoringLayout.Metrics();

    private int mTextSize;
    private int mTextColor;
    private String mPositionDistanceFromText;
    private String mDistanceText;
    private CharSequence mCommercialDistrictText;

    private Layout mPositionDistanceFromTextLayout;
    private Layout mDistanceTextLayout;
    private Layout mCommercialDistrictTextLayout;

    private int mCommercialDistrictMarginLeft = 0;

    private TextPaint mTextPaint = new TextPaint();

    public HotelLocationTextView(Context context) {
        this(context, null);
    }

    public HotelLocationTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotelLocationTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HotelLocationTextView, defStyleAttr, 0);
        mTextSize = a.getDimensionPixelSize(R.styleable.HotelLocationTextView_textSize, 0);
        mTextColor = a.getColor(R.styleable.HotelLocationTextView_textColor, 0);
        mCommercialDistrictMarginLeft = a.getDimensionPixelSize(R.styleable.HotelLocationTextView_commercialDistrictMarginLeft, 0);
        a.recycle();

        mTextPaint.setAntiAlias(true);
        Resources res = context.getResources();
       // mTextPaint.density = res.getDisplayMetrics().density;
    }

    public void setPositionDistanceFromText(String text) {
        if (mPositionDistanceFromText != text && (mPositionDistanceFromText == null || !mPositionDistanceFromText.equals(text))) {
            mPositionDistanceFromText = text;
            requestLayout();
            invalidate();
        }
    }

    public void setDistanceText(String text) {
        if (mDistanceText != text && (mDistanceText == null || !mDistanceText.equals(text))) {
            mDistanceText = text;
            requestLayout();
            invalidate();
        }
    }

    public void setCommercialDistrictText(CharSequence text) {
        if (mCommercialDistrictText != text && (mCommercialDistrictText == null || !mCommercialDistrictText.equals(text))) {
            mCommercialDistrictText = text;
            requestLayout();
            invalidate();
        }
    }

    private void nullLayouts() {
        mPositionDistanceFromTextLayout = mDistanceTextLayout = mCommercialDistrictTextLayout = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();
        int textWidth = width - hPadding;

        nullLayouts();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        String positionDistanceFromText = mPositionDistanceFromText;
        if (!isLocationOrCityCenter()) {
            float textLength = measureText(mTextPaint, positionDistanceFromText) + measureText(mTextPaint, mDistanceText) + measureText(mTextPaint, mCommercialDistrictText);
            if (!TextUtils.isEmpty(mCommercialDistrictText)) {
                textLength += mCommercialDistrictMarginLeft;
            }
            if (textLength > textWidth && !TextUtils.isEmpty(positionDistanceFromText)) {
                positionDistanceFromText = "距目的地";
            }
        }

        if (!TextUtils.isEmpty(mDistanceText)) {
            mDistanceTextLayout = makeSingleLayout(mDistanceText, mTextPaint, textWidth);
            textWidth -= mDistanceTextLayout.getLineWidth(0);
        }
        if (!TextUtils.isEmpty(positionDistanceFromText)) {
            mPositionDistanceFromTextLayout = makeSingleLayout(positionDistanceFromText, mTextPaint, textWidth);
            textWidth -= mPositionDistanceFromTextLayout.getLineWidth(0);
        }
        if (!TextUtils.isEmpty(mCommercialDistrictText)) {
            textWidth -= mCommercialDistrictMarginLeft;
            mCommercialDistrictTextLayout = makeSingleLayout(mCommercialDistrictText, mTextPaint, textWidth);
        }

        int height = mPositionDistanceFromTextLayout == null ? 0 : getDesiredHeight(mPositionDistanceFromTextLayout);
        height = Math.max(height, mDistanceTextLayout == null ? 0 : getDesiredHeight(mDistanceTextLayout));
        height = Math.max(height, mCommercialDistrictTextLayout == null ? 0 : getDesiredHeight(mCommercialDistrictTextLayout));
        height += vPadding;

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPositionAndDistanceText(canvas);
        drawCommercialDistrictText(canvas);
    }

    private void drawPositionAndDistanceText(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (mPositionDistanceFromTextLayout != null) {
            mPositionDistanceFromTextLayout.draw(canvas);
            canvas.translate(mPositionDistanceFromTextLayout.getLineWidth(0), 0);
        }
        if (mDistanceTextLayout != null) {
            mDistanceTextLayout.draw(canvas);
        }
        canvas.restore();

    }

    private void drawCommercialDistrictText(Canvas canvas) {
        if (mCommercialDistrictTextLayout != null) {
            canvas.save();
            int left = getWidth() - getPaddingRight() - Math.round(mCommercialDistrictTextLayout.getLineWidth(0));
            canvas.translate(left, getPaddingTop());
            mCommercialDistrictTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    private boolean isLocationOrCityCenter() {
        return TextUtils.isEmpty(mPositionDistanceFromText) || mPositionDistanceFromText.startsWith("距您") || mPositionDistanceFromText.startsWith("距市中心");
    }

    private static float measureText(Paint paint, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            return paint.measureText(text, 0, text.length());
        }
        return 0f;
    }

    private static Layout makeSingleLayout(CharSequence text, TextPaint paint, int width) {
        BoringLayout.Metrics boring = BoringLayout.isBoring(text, paint);
        if (boring == null) {
            boring = UNKNOWN_BORING;
        }
        return BoringLayout.make(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f,
                boring, false, TextUtils.TruncateAt.END, width);
    }

    public static CharSequence stringOrSpannedString(CharSequence source) {
        if (source == null)
            return null;
        if (source instanceof SpannedString)
            return source;
        if (source instanceof Spanned)
            return new SpannedString(source);

        return source.toString();
    }

    private int getDesiredHeight(Layout layout) {
        if (layout == null) {
            return 0;
        }

        int lineCount = layout.getLineCount();
        int pad = getPaddingTop() + getPaddingBottom();
        int desired = layout.getLineTop(lineCount);
        desired += pad;

        return desired;
    }
}
