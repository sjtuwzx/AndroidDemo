package com.wzx.android.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wang_zx on 2015/1/19.
 */
public class MyTextView extends View {

    private static final String TAG = MyTextView.class.getSimpleName();

    private Layout mLayout;

    private Layout mMoreLayout;

    String content = "取消bug除了1个详情跳转日历bug属于历史缺陷外，其他属于历史缺陷外" +
            "涉及到凌晨订单业务需求引起的，此处确实自测不足，由于当初开发完了日历相关功能优化，自测其他逻辑ok，只是对于凌晨订单这块业务本来不是太清楚 " +
            "同时自测凌晨订单这个需要服务端提供，而服务又依赖于公共框架那边的服务，导致凌晨订单的自测环境没有， 所以这块是自测没有 (ios " +
            "同样的自测不了），我们也催促测试和服务跟进，但是由于开发阶段还有对外依赖， 一直没有给我们凌晨订单环境，同时后来又来了如家的";

    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TextPaint paint = new TextPaint();
        paint.setColor(0xFF00FFFF);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        mMoreLayout = new DynamicLayout("更多^", paint, 500,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        setBackgroundColor(0x80000000);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        TextPaint paint = new TextPaint();
        paint.setColor(0xFFFF00FF);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        mLayout = new MyLayout(content, paint, getMeasuredWidth());

        int height = mLayout.getHeight();

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLayout.draw(canvas);
        canvas.save();
        canvas.translate(mLayout.getLineRight(mLayout.getLineCount() - 1), mLayout.getLineTop(mLayout.getLineCount()
                - 1));
        mMoreLayout.draw(canvas);
        canvas.restore();
    }


    private int mEllipsisCount = 3;

    private class MyLayout extends DynamicLayout {

        private int mMaxLines = 3;

        public MyLayout(String base, TextPaint paint, int width) {
            super(base, base, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false,
                    TextUtils.TruncateAt.END, 0);

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
            if (line == mMaxLines - 1) {
                return mEllipsisCount;
            }
            return 0;
        }

        @Override
        public int getEllipsisStart(int line) {
            if (line == mMaxLines - 1) {
                return getLineEnd(line) - getLineStart(line) - mEllipsisCount;
            }
            return 0;
        }


    }
}
