package com.wzx.android.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Created by wang_zx on 2015/8/17.
 */
public class DrawableCenterTextView extends TextView {

    public DrawableCenterTextView(Context context) {
        this(context, null);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            Drawable drawableRight = drawables[2];
            if (drawableLeft != null || drawableRight != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                if (drawableLeft != null) {
                    Rect bounds = drawableLeft.getBounds();
                    drawableWidth = bounds.width();
                } else if (drawableRight != null) {
                    Rect bounds = drawableRight.getBounds();
                    drawableWidth = bounds.width();
                }
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                canvas.translate((bodyWidth - getWidth()) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}
