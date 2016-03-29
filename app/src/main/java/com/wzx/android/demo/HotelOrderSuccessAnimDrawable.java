package com.wzx.android.demo;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class HotelOrderSuccessAnimDrawable extends Drawable {

	private float mFactor = 1.0f;
	private Paint mCirclePaint = new Paint();
	private Paint mTickPaint = new Paint();
	private Path mTickPath = new Path();
	
	public HotelOrderSuccessAnimDrawable(int color, int width) {
		mCirclePaint.setColor(color);
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(width);
		mCirclePaint.setAntiAlias(true);

		mTickPaint.setColor(color);
		mTickPaint.setStyle(Style.STROKE);
		mTickPaint.setStrokeWidth(width * 1.5f);
		mTickPaint.setAntiAlias(true);
	}
	
	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		mCirclePaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return PixelFormat.TRANSLUCENT;
	}
	
	public void setFactor(float factor) {
		mFactor = factor;
		invalidateSelf();
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		drawCircle(canvas, mFactor);
		
		if (mFactor > 0.5f) {
			drawTick(canvas, (mFactor - 0.5f) / 0.5f);
		}
	}
	
	private void drawCircle(Canvas canvas, float factor) {
		Rect bounds = getBounds();
		RectF oval = new RectF();
		float strokeWidth = mCirclePaint.getStrokeWidth();
		oval.set(bounds.left + strokeWidth / 2, bounds.top + strokeWidth / 2, 
				bounds.right - strokeWidth / 2, bounds.bottom - strokeWidth / 2);
		
		float startAngle = -90f;
		if (factor > 0.33f && factor < 0.36f) {
			startAngle = -90f + 360f * (factor - 0.33f) / 0.03f;
		}
		float endAngle = -90f;
		if (factor < 0.36f) {
			endAngle =  -90f + 360f * factor / 0.36f;
		} else if (factor > 0.5f) {
			endAngle =  -90f + 360f * (factor - 0.5f) / 0.5f;
		}
		
		canvas.drawArc(oval, startAngle, endAngle - startAngle, false, mCirclePaint);
	}
	
	private static final float[][] sTickCoordinates = {{0.238f , 0.5f}, {0.429f, 0.69f}, {0.738f, 0.31f}};
	
	private void drawTick(Canvas canvas, float factor) {
		Rect bounds = getBounds();
		float width = bounds.width();
		float height = bounds.height();

		mTickPath.reset();
		mTickPath.moveTo(width * sTickCoordinates[0][0], height * sTickCoordinates[0][1]);
		lineTickPath(mTickPath, width, height, 0, Math.min(factor / 0.8f, 1.0f));
		if (factor > 0.8f) {
			lineTickPath(mTickPath, width, height, 1, (factor - 0.8f) / 0.2f);
		}

		canvas.drawPath(mTickPath, mTickPaint);
	}
	
	private void lineTickPath(Path path, float width, float height, int index, float factor) {
		float stopX = width * (sTickCoordinates[index][0] + (sTickCoordinates[index + 1][0] - sTickCoordinates[index][0]) * factor);
		float stopY = height * (sTickCoordinates[index][1] + (sTickCoordinates[index + 1][1] - sTickCoordinates[index][1]) * factor);
		path.lineTo(stopX, stopY);
	}

}
