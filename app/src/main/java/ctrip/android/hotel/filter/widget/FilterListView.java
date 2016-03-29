package ctrip.android.hotel.filter.widget;

import ctrip.android.hotel.filter.DeviceInfoUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FilterListView extends ListView {

	public static final int MODE_HEADER = 1;

	public static final int MODE_MIDDLE = 2;

	public static final int MODE_TAIL = 3;

	public static interface FilterAdapter {
		
		public int getMode();

		public int getActiviePosition();

	}

	private FilterAdapter mAdapter;
	
	private int mBorderWidth;
	private Paint mPaint = new Paint();

	public FilterListView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public FilterListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public FilterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mBorderWidth = DeviceInfoUtil.getPixelFromDip(0.5f);
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xFFDDDDDD);
		mPaint.setStrokeWidth(mBorderWidth);
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
        mAdapter = (FilterAdapter) adapter;
		super.setAdapter(adapter);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		drawBorder(canvas);
	}

	private void drawBorder(Canvas canvas) {
		if (mAdapter == null) {
			return;
		}
		int mode = mAdapter.getMode();
		if (mode == MODE_TAIL) {
			return;
		}
		int activePosition = mAdapter.getActiviePosition();
		int activieChildIndex = activePosition
				- getFirstVisiblePosition();
		View child = getChildAt(activieChildIndex);

		int width = getWidth() - mBorderWidth;
		int height = getHeight();
		if (child == null) {
			canvas.drawLine(width, 0, width, height, mPaint);
		} else if (mode == MODE_HEADER) {
			canvas.drawLine(width, 0, width, child.getTop(), mPaint);
			canvas.drawLine(width, child.getBottom(), width, height, mPaint);
		} else if (mode == MODE_MIDDLE) {
			int childHeight = child.getBottom() - child.getTop();
			int shapeHeight = DeviceInfoUtil.getPixelFromDip(10);
			int shapeWidth = DeviceInfoUtil.getPixelFromDip(7);
			int compensation = (childHeight - shapeHeight) / 2;
			canvas.drawLine(width, 0, width, child.getTop() + compensation,
					mPaint);
			canvas.drawLine(width, child.getTop() + compensation, width
					- shapeWidth, child.getTop() + childHeight / 2, mPaint);
			canvas.drawLine(width - shapeWidth, child.getTop() + childHeight
					/ 2, width, child.getBottom() - compensation, mPaint);
			canvas.drawLine(width, child.getBottom() - compensation, width,
					height, mPaint);
		}
	}

}
