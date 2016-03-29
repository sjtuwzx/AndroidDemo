package ctrip.android.hotel.filter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.sender.filter.AllFilterNode;
import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;
import ctrip.android.hotel.sender.filter.FilterRoot;
import ctrip.android.hotel.sender.filter.UnlimitedFilterNode;

public class FilterTreeView extends ViewGroup implements AdapterView.OnItemClickListener {

    private FilterGroup mFilterGroup;
    private FilterTreeView mSubTreeView;
    private InternalListView mListView;
    private ProgressBar mProgressBar;
    private FilterListAdapter mFilterListAdapter;

    private int mBorderWidth;
    private Paint mPaint = new Paint();

    private boolean mContainUnlimitedNode = true;
    private boolean mIndicateSelectState = true;

    private TreeViewConfig mTreeViewConfig;

    public FilterTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFilterListAdapter = new FilterListAdapter(context);

        mListView = new InternalListView(context);
        mListView.setSelector(R.drawable.hotel_tree_list_selector);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView paramAbsListView,
                                             int paramInt) {
            }

            @Override
            public void onScroll(AbsListView paramAbsListView, int paramInt1,
                                 int paramInt2, int paramInt3) {
                invalidate();
            }
        });
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mFilterListAdapter);

        addView(mListView);
        initInfoPanel();

        mBorderWidth = DeviceInfoUtil.getPixelFromDip(0.5f);
        mPaint.setColor(0xFFDDDDDD);
        mPaint.setStrokeWidth(mBorderWidth);
    }

    private void initInfoPanel() {
        mProgressBar = new ProgressBar(getContext());
        mProgressBar.setIndeterminateDrawable(getContext().getResources()
                .getDrawable(R.drawable.loading_drawable_progress));
        mProgressBar.setVisibility(View.GONE);

        addView(mProgressBar);
    }

    private void makeAndAddSubTree() {
        mSubTreeView = new FilterTreeView(getContext(), null);
        mSubTreeView.setBackgroundColor(Color.WHITE);

        mSubTreeView.setContainUnlimitedNode(mContainUnlimitedNode);
        mSubTreeView.setIndicateSelectState(mIndicateSelectState);
        mSubTreeView.setLazyLoader(mLazyLoader);
        mSubTreeView.setOnItemClickListener(mOnItemClickListener);

        addView(mSubTreeView);
    }

    public void setContainUnlimitedNode(boolean containUnlimitedNode) {
        mContainUnlimitedNode = containUnlimitedNode;
        if (mSubTreeView != null) {
            mSubTreeView.setContainUnlimitedNode(containUnlimitedNode);
        }
    }

    public void setIndicateSelectState(boolean indicateSelectState) {
        mIndicateSelectState = indicateSelectState;
        if (mSubTreeView != null) {
            mSubTreeView.setIndicateSelectState(indicateSelectState);
        }
    }

    public void refresh() {
        mFilterListAdapter.notifyDataSetChanged();
        if (mSubTreeView != null) {
            mSubTreeView.refresh();
        }
    }

    public void setFilterGroup(FilterGroup group) {
        mFilterGroup = group;
        mTreeViewConfig = (TreeViewConfig) mFilterGroup.getTag();

        int dividerColor = mTreeViewConfig.dividerColor;
        if (dividerColor == -1) {
            mListView.setDivider(null);
        } else {
            ColorDrawable divider = new ColorDrawable(dividerColor);
            mListView.setDivider(divider);
            mListView.setDividerHeight(DeviceInfoUtil.getPixelFromDip(.5f));
        }
        int padding = mTreeViewConfig.padding;
        mListView.setPadding(padding, 0, padding, 0);
        mListView.setVisibility(VISIBLE);

        /*mListView.setAdapter(mFilterListAdapter);*/
        mFilterListAdapter.setFilterGroup(mFilterGroup, mContainUnlimitedNode, mIndicateSelectState);

        int selectChildPosition = mFilterGroup
                .getFirstSelectChildPosition(mContainUnlimitedNode);

        if (openSubTree(Math.max(0, selectChildPosition))) {
            mListView.setSelectPosition(Math.max(0, selectChildPosition - 3));
        } else {
            mListView.setSelectPosition(0);
        }

        requestLayout();
    }


    public boolean openSubTree(int position) {
        if (position >= mFilterListAdapter.getCount()) {
            return false;
        }
        FilterNode child = mFilterListAdapter.getItem(position);
        if (child instanceof FilterGroup) {
            mFilterListAdapter.setActivePosition(position);

            FilterGroup childGroup = (FilterGroup) child;
            if (mSubTreeView == null) {
                makeAndAddSubTree();
            }
            mSubTreeView.setVisibility(VISIBLE);
            if (childGroup.canOpen() && !childGroup.hasOpened()) {
                mSubTreeView.mListView.setVisibility(GONE);
                if (mSubTreeView.mSubTreeView != null) {
                    mSubTreeView.mSubTreeView.setVisibility(GONE);
                }
                mSubTreeView.mProgressBar.setVisibility(VISIBLE);
                if (mLazyLoader != null) {
                    mLazyLoader.lazyLoad(this, childGroup, position);
                }
            } else {
                mSubTreeView.setFilterGroup(childGroup);
                mSubTreeView.mProgressBar.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int navWidth = width;
        if (mTreeViewConfig != null) {
            navWidth = (int) (width * mTreeViewConfig.widthWeight);
        }

        int childWidth = width - navWidth;
        if (mListView != null) {
            int widthSpec = MeasureSpec.makeMeasureSpec(navWidth,
                    MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
            mListView.measure(widthSpec, heightSpec);
        }

        int pgWidthSpec = MeasureSpec.makeMeasureSpec(
                LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
        int pgHeightSpec = MeasureSpec.makeMeasureSpec(
                LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
        mProgressBar.measure(pgWidthSpec, pgHeightSpec);
        if (mSubTreeView != null) {
            int widthSpec = MeasureSpec.makeMeasureSpec(childWidth,
                    MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
            mSubTreeView.measure(widthSpec, heightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = b - t;
        int navWidth = width;
        if (mTreeViewConfig != null) {
            navWidth = (int) (width * mTreeViewConfig.widthWeight);
        }

        if (mListView != null) {
            mListView.layout(0, 0, navWidth, height);

        }
        if (mSubTreeView != null) {
            mSubTreeView.layout(navWidth, 0, r, height);
        }
        int mProgressBarWidth = mProgressBar.getMeasuredWidth();
        int mProgressBarHeight = mProgressBar.getMeasuredHeight();
        int mStartX = (width - mProgressBarWidth) / 2;
        int startY = (height - mProgressBarHeight) / 2;
        mProgressBar.layout(mStartX, startY, mStartX + mProgressBarWidth,
                startY + mProgressBarHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawBorder(canvas);
    }

    private void drawBorder(Canvas canvas) {
        if (mListView.getVisibility() != View.VISIBLE
                || mFilterListAdapter.getCount() <= 0) {
            return;
        }
        int activePosition = mFilterListAdapter.getActivePosition();
        int activeChildIndex = activePosition
                - mListView.getFirstVisiblePosition();
        View child = mListView.getChildAt(activeChildIndex);

        int height = getHeight();
        int width = mListView.getWidth();
        if (child == null) {
            canvas.drawLine(width, 0, width, height, mPaint);
        } else if (mFilterGroup instanceof FilterRoot) {
            canvas.drawLine(width, 0, width, child.getTop(), mPaint);
            canvas.drawLine(width, child.getBottom(), width, height, mPaint);
        } else {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        FilterNode node = mFilterListAdapter.getItem(position);

        if (node.isLeaf()) {
            if (node.isSelected()
                    && (node instanceof UnlimitedFilterNode || node instanceof AllFilterNode)) {
                return;
            }
            if (!node.isSelected() || !mFilterGroup.isSingleChoice()) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onLeafItemClick(this, view, mFilterGroup, node, position);
                }
            }
        } else if (mFilterListAdapter.getActivePosition() != position) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onGroupItemClick(this, view, mFilterGroup, (FilterGroup) node, position);
            }
        }
    }

    public void onSubTreeLoadSuccess(FilterGroup childGroup, int position) {
        if (mFilterGroup.contain(childGroup, true)
                && mFilterListAdapter != null
                && mFilterListAdapter.getActivePosition() == position) {
            openSubTree(position);
        }
    }

    public void onSubTreeLoadFail(FilterGroup childGroup, int position) {
        if (mFilterGroup.contain(childGroup, true)
                && mFilterListAdapter != null
                && mFilterListAdapter.getActivePosition() == position
                && mSubTreeView != null) {
            mSubTreeView.mProgressBar.setVisibility(View.GONE);
        }
    }

    public static class TreeViewConfig {
        public int dividerColor = -1;
        public float widthWeight = 1.0f;
        public int itemMinHeight = 0;
        public int padding = 0;
    }

    private LazyLoader mLazyLoader;

    public void setLazyLoader(LazyLoader loader) {
        mLazyLoader = loader;
        if (mSubTreeView != null) {
            mSubTreeView.setLazyLoader(mLazyLoader);
        }
    }

    public static interface LazyLoader {
        public void lazyLoad(FilterTreeView treeView, FilterGroup group,
                             int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        if (mSubTreeView != null) {
            mSubTreeView.setOnItemClickListener(mOnItemClickListener);
        }
    }

    public static interface OnItemClickListener {
        void onLeafItemClick(FilterTreeView treeView, View view,
                             FilterGroup parent, FilterNode node, int position);

        void onGroupItemClick(FilterTreeView treeView, View view,
                              FilterGroup parent, FilterGroup group, int position);
    }

    private static class InternalListView extends ListView {

        private int mSelectPosition = -1;

        public InternalListView(Context context) {
            super(context);
        }
        public void setSelectPosition(int position) {
            mSelectPosition = position;
        }

        @Override
        protected void layoutChildren() {
            if (mSelectPosition >= 0) {
                setSelection(mSelectPosition);
                mSelectPosition = -1;
            }
            super.layoutChildren();
        }
    }

}
