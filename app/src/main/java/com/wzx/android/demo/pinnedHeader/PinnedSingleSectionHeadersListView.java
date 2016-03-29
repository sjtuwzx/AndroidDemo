package com.wzx.android.demo.pinnedHeader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by wang_zx on 2015/3/11.
 */
public class PinnedSingleSectionHeadersListView extends ListView implements View.OnClickListener {

    public static interface PinnedSingleSectionedHeaderAdapter {

        public View getPinnedHeaderView(int position, View convertView,
                                        ViewGroup parent);
        public int getHeaderCount();

        public void setOnDataChangeListener(SingleSectionBaseAdapter.OnDataChangeListener listener);

    }

    private SparseArray<View> mHeaderViews = new SparseArray<View>();
    private PinnedSingleSectionedHeaderAdapter mAdapter;
    private int mWidthMode;
    private int mHeightMode;

    private SparseArray<PinnedHeaderTouchHelper> mPinnedHeaderTouchHelpers = new SparseArray<PinnedHeaderTouchHelper>();

    private SingleSectionBaseAdapter.OnDataChangeListener mOnDataChangeListener = new SingleSectionBaseAdapter.OnDataChangeListener() {
        @Override
        public void onChange(SingleSectionBaseAdapter adapter) {
            reset();
        }
    };

    public PinnedSingleSectionHeadersListView(Context context) {
        this(context, null);
    }

    public PinnedSingleSectionHeadersListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedSingleSectionHeadersListView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        reset();
        if (adapter instanceof PinnedSingleSectionedHeaderAdapter) {
            mAdapter = (PinnedSingleSectionedHeaderAdapter) adapter;
            mAdapter.setOnDataChangeListener(mOnDataChangeListener);
        }
        super.setAdapter(adapter);
    }

    private void reset() {
        if (mHeaderViews != null) {
            mHeaderViews.clear();
        }
        if (mPinnedHeaderTouchHelpers != null) {
            int N = mPinnedHeaderTouchHelpers.size();
            for (int i = 0; i < N; i++) {
                PinnedHeaderTouchHelper helper = mPinnedHeaderTouchHelpers.valueAt(i);
                helper.reset();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        mHeightMode = View.MeasureSpec.getMode(heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int N = mHeaderViews.size();
        for (int i = 0; i < N; i++) {
            View header = mHeaderViews.valueAt(i);
            ensurePinnedHeaderLayout(header, true);
        }
    }

    private View getHeaderView(int position) {
        if (mHeaderViews.indexOfKey(position) < 0 && position < mAdapter.getHeaderCount()) {
            View view = mAdapter.getPinnedHeaderView(position, null, this);
            view.setContentDescription(String.valueOf(position));
            view.setOnClickListener(this);

            ensurePinnedHeaderLayout(view, false);
            ensureAttachedToWindow(view);
            mHeaderViews.put(position, view);
        }
        return mHeaderViews.get(position);
    }

    @Override
    public void onClick(View v) {
        AdapterView.OnItemClickListener listener = getOnItemClickListener();
        if (listener != null) {
            String contentDescription = (String) v.getContentDescription();
            int position = 0;
            try {
                position = Integer.valueOf(contentDescription);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            listener.onItemClick(this, v, position, v.getId());
        }
    }

    private void ensurePinnedHeaderLayout(View header, boolean focus) {
        if (focus || header.isLayoutRequested()) {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft() - getPaddingRight(),
                    mWidthMode);

            int heightSpec;
            ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height,
                        View.MeasureSpec.EXACTLY);
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
            }
            header.measure(widthSpec, heightSpec);
            if (focus) {
                header.layout(header.getLeft(), header.getTop(),
                        header.getLeft() + header.getMeasuredWidth(),
                        header.getTop() + header.getMeasuredHeight());
            } else {
                header.layout(0, -1, header.getMeasuredWidth(),
                        header.getMeasuredHeight() - 1);
            }
        }
    }

    private void ensureAttachedToWindow(View child) {

        if (!child.hasWindowFocus()) {
            try {
                Class<?> clazz = View.class;
                Field field = clazz.getDeclaredField("mAttachInfo");
                field.setAccessible(true);
                Object attachInfo = field.get(this);
                if (attachInfo != null) {
                    Class<?> clz = ViewGroup.class;
                    Method method = clz.getDeclaredMethod(
                            "dispatchAttachedToWindow", attachInfo.getClass(), int.class);
                    method.setAccessible(true);
                    method.invoke(child, attachInfo, View.VISIBLE);
                    method.setAccessible(false);
                }
                field.setAccessible(false);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int dividerHeight = Math.max(0, getDividerHeight());
        Drawable divider = getDivider();
        if (mAdapter != null) {
            int headerCount = mAdapter.getHeaderCount();
            int marginTop = 0;
            for (int i = 0; i < headerCount; i++) {
                View header = getHeaderView(i);
                int saveCount = canvas.save();
                canvas.translate(getPaddingLeft(), marginTop);
                canvas.clipRect(0, 0, header.getWidth(),
                        header.getHeight() + dividerHeight);

                header.draw(canvas);
                if (divider != null) {
                    divider.setBounds(0, header.getHeight(), header.getWidth(), header.getHeight() + dividerHeight);
                    divider.draw(canvas);
                    marginTop += header.getHeight() + dividerHeight;
                }

                canvas.restoreToCount(saveCount);

            }
        }
    }

    private PinnedHeaderTouchHelper getPinnedHeaderTouchHelper(int position) {
        if (mPinnedHeaderTouchHelpers.indexOfKey(position) < 0) {
            PinnedHeaderTouchHelper helper = new PinnedHeaderTouchHelper(this);
            mPinnedHeaderTouchHelpers.put(position, helper);
        }
        return mPinnedHeaderTouchHelpers.get(position);
    }

    @TargetApi(14)
    @Override
    public boolean shouldDelayChildPressedState() {
        // TODO Auto-generated method stub
        int N = mPinnedHeaderTouchHelpers.size();
        for (int i = 0; i < N; i++) {
            PinnedHeaderTouchHelper helper = mPinnedHeaderTouchHelpers.valueAt(i);
            if (!helper.shouldDelayChildPressedState()) {
                return false;
            }
        }
        return super.shouldDelayChildPressedState();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mAdapter == null) {
            return super.dispatchTouchEvent(ev);
        }
        int headerCount = mAdapter.getHeaderCount();
        if (headerCount == 0) {
            return super.dispatchTouchEvent(ev);
        }

        int dividerHeight = getDividerHeight();
        Drawable divider = getDivider();
        int marginTop = 0;
        for (int i = 0; i < headerCount; i++) {
            View header = getHeaderView(i);
            PinnedHeaderTouchHelper helper = getPinnedHeaderTouchHelper(i);

            Rect rect = new Rect(getPaddingLeft(), marginTop, getPaddingLeft() + header.getWidth(),
                    header.getHeight() + marginTop);
            if (helper.dispatchPinnedHeaderTouchEvent(header, rect, ev, -getPaddingLeft(),
                     -marginTop)) {
                return true;
            }
            marginTop += header.getHeight();
            if (divider != null && dividerHeight > 0) {
                marginTop += dividerHeight;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
