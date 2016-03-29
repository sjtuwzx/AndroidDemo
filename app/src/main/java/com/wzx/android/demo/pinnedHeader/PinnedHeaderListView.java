package com.wzx.android.demo.pinnedHeader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PinnedHeaderListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = PinnedHeaderListView.class.getSimpleName();

    public static final String TAG_PINNED_HEADER = "pinned_header";

    public interface PinnedSectionedHeaderAdapter {

        int getSectionForPosition(int position);

        View getSectionPinnedHeaderView(int section, View convertView,
                                        ViewGroup parent);

        int getSectionHeaderViewType(int section);

        int getCount();

        boolean hasSectionHeader(int section);

        boolean shouldPinSectionHeader(int section);

        boolean isSectionHeader(int position);

        boolean isSectionFirst(int position);

        int getSectionFirstPosition(int section);

        View getDoublePinnedHeaderView(int position, ViewGroup parent);

    }

    protected PinnedSectionedHeaderAdapter mAdapter;
    private View mCurrentHeader;
    private int mCurrentHeaderViewType = 0;
    private int mHeaderOffset;
    private int mCurrentSection = 0;
    private int mWidthMode;
    private int mHeightMode;

    protected int mPinnedHeaderMarginTop = 0;

    private PinnedHeaderTouchHelper mPinnedHeaderTouchHelper;

    public PinnedHeaderListView(Context context) {
        this(context, null);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mPinnedHeaderTouchHelper = new PinnedHeaderTouchHelper(this);
        setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mCurrentHeader = null;
        if (adapter instanceof PinnedSectionedHeaderAdapter) {
            mAdapter = (PinnedSectionedHeaderAdapter) adapter;
        }
        super.setAdapter(adapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        privateOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    protected void privateOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mCurrentHeader != null) {
            showView(mCurrentHeader);
        }

        int firstVisiblePositionForAdapter = firstVisibleItem - getHeaderViewsCount();

        if (mPinnedHeaderMarginTop > 0) {
            int N = getChildCount();
            for (int i = 0; i < N; i++) {
                View child = getChildAt(i);
                if (child.getBottom() <= mPinnedHeaderMarginTop) {
                    ++firstVisiblePositionForAdapter;
                } else {
                    break;
                }
            }
        }

        if (mAdapter == null || mAdapter.getCount() == 0
                || firstVisiblePositionForAdapter < 0 || firstVisiblePositionForAdapter >= mAdapter.getCount()) {
            resetPinnedHeader();
            return;
        }

        int section = mAdapter.getSectionForPosition(firstVisiblePositionForAdapter);
        int viewType = mAdapter.getSectionHeaderViewType(section);
        mCurrentHeader = getSectionHeaderView(section,
                mCurrentHeaderViewType != viewType ? null : mCurrentHeader);
        if (mCurrentHeader == null) {
            resetPinnedHeader();
            return;
        } else {
            int headerPosition = mAdapter.getSectionFirstPosition(section);
            View child = getChildAt(headerPosition + getHeaderViewsCount() - firstVisibleItem);
            if (child != null && child.getTop() == mPinnedHeaderMarginTop) {
                resetPinnedHeader();
                return;
            }
        }
        ensurePinnedHeaderLayout(mCurrentHeader, false);
        ensureAttachedToWindow(mCurrentHeader);
        mCurrentHeaderViewType = viewType;

        mHeaderOffset = 0;

        firstVisibleItem -= getHeaderViewsCount();
        View preHideView = null;
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (i >= 0 && mAdapter.isSectionFirst(i) || i >= mAdapter
                    .getCount()) {
                View header = getChildAt(i - firstVisibleItem);
                showView(header);
                if (mCurrentHeader != null) {
                    int headerTop = header.getTop();
                    int pinnedHeaderHeight = mCurrentHeader.getHeight();
                    if (i >= firstVisiblePositionForAdapter && mPinnedHeaderMarginTop + pinnedHeaderHeight > headerTop
                            && headerTop >= mPinnedHeaderMarginTop) {
                        if (mHeaderOffset == 0) {
                            mHeaderOffset = headerTop
                                    - (mPinnedHeaderMarginTop + pinnedHeaderHeight);
                        }
                    } else if (mAdapter.isSectionHeader(i) && headerTop < mPinnedHeaderMarginTop) {
                        if (preHideView != null) {
                            showView(preHideView);
                        }

                        int sectionForPosition = mAdapter.getSectionForPosition(i);
                        if (mAdapter.shouldPinSectionHeader(sectionForPosition)) {
                            preHideView = header;
                            hideView(header);
                        }
                    }
                }
            }
        }

    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = section != mCurrentSection || oldView == null;

        if (!mAdapter.hasSectionHeader(section) || !mAdapter.shouldPinSectionHeader(section))
            return null;
        View view = mAdapter.getSectionPinnedHeaderView(section, oldView, this);
        if (shouldLayout) {
            // a new section, thus a new header. We should lay it out again
            ensurePinnedHeaderLayout(view, false);
            ensureAttachedToWindow(view);
            mCurrentSection = section;
        }
        return view;
    }

    protected void ensurePinnedHeaderLayout(View header, boolean focus) {
        if (focus || header.isLayoutRequested()) {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth(),
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

    protected void ensureAttachedToWindow(View child) {

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

    private void resetPinnedHeader() {
        mCurrentHeader = null;
        mHeaderOffset = 0;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View header = getChildAt(i);
            if (header != null) {
                showView(header);
            }
        }
    }

    protected void hideView(View v) {
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                v.setAlpha(0);
            } else {
                AlphaAnimation anim = new AlphaAnimation(1, 0);
                anim.setDuration(0);
                anim.setFillAfter(true);
                v.startAnimation(anim);
            }
        }
    }

    protected void showView(View v) {
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                v.setAlpha(1);
            } else {
                AlphaAnimation anim = new AlphaAnimation(0, 1);
                anim.setDuration(0);
                anim.setFillAfter(true);
                v.startAnimation(anim);
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
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        if (mCurrentHeader != null) {
            ensurePinnedHeaderLayout(mCurrentHeader, true);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mAdapter != null && mCurrentHeader != null) {
            int saveCount = canvas.save();
            canvas.translate(0, mPinnedHeaderMarginTop + mHeaderOffset);
            canvas.clipRect(0, 0, getWidth(),
                    mCurrentHeader.getHeight());

            mCurrentHeader.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }

    @TargetApi(14)
    @Override
    public boolean shouldDelayChildPressedState() {
        // TODO Auto-generated method stub
        return mPinnedHeaderTouchHelper.shouldDelayChildPressedState() && super.shouldDelayChildPressedState();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mCurrentHeader == null) {
           return dispatchListViewTouchEvent(ev);
        }

        Rect rect = new Rect(0, mPinnedHeaderMarginTop + mHeaderOffset, mCurrentHeader.getWidth(),
                mCurrentHeader.getHeight() + mPinnedHeaderMarginTop + mHeaderOffset);
        if (mPinnedHeaderTouchHelper.dispatchPinnedHeaderTouchEvent(mCurrentHeader, rect, ev, 0,
                -mHeaderOffset - mPinnedHeaderMarginTop)) {
            return true;
        } else {
            return dispatchListViewTouchEvent(ev);
        }
    }


    private boolean mHasTouchDownListView = false;
    protected boolean dispatchListViewTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            mHasTouchDownListView = true;
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
            mHasTouchDownListView = false;
            return super.dispatchTouchEvent(ev);
        }
        if (mHasTouchDownListView) {
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }

    public void setPinnedHeaderMarginTop(int marginTop) {
        mPinnedHeaderMarginTop = marginTop;
        privateOnScroll(this, getFirstVisiblePosition(), getLastVisiblePosition() - getFirstVisiblePosition() + 1, getCount());
        invalidate();
    }

    public void setOnItemClickListener(
            PinnedHeaderListView.OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    public static abstract class OnItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int rawPosition, long id) {
            SectionedBaseAdapter adapter;
            if (adapterView.getAdapter().getClass()
                    .equals(HeaderViewListAdapter.class)) {
                HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) adapterView
                        .getAdapter();
                adapter = (SectionedBaseAdapter) wrapperAdapter
                        .getWrappedAdapter();
            } else {
                adapter = (SectionedBaseAdapter) adapterView.getAdapter();
            }
            int section = adapter.getSectionForPosition(rawPosition);
            int position = adapter.getPositionInSectionForPosition(rawPosition);

            if (position == -1) {
                onSectionClick(adapterView, view, section, id);
            } else {
                onItemClick(adapterView, view, section, position, id);
            }
        }

        public abstract void onItemClick(AdapterView<?> adapterView, View view,
                                         int section, int position, long id);

        public abstract void onSectionClick(AdapterView<?> adapterView,
                                            View view, int section, long id);

    }

    static class SavedState extends View.BaseSavedState {
        int pinnedHeaderMarginTop;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            pinnedHeaderMarginTop = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(pinnedHeaderMarginTop);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
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

        ss.pinnedHeaderMarginTop = mPinnedHeaderMarginTop;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mPinnedHeaderMarginTop = ss.pinnedHeaderMarginTop;
    }



    @Override
    protected void removeDetachedView(View child, boolean animate) {
        // TODO Auto-generated method stub
        if (!TAG_PINNED_HEADER.equals(child.getContentDescription())) {
            super.removeDetachedView(child, animate);
        }
    }

}
