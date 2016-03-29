/*
package ctrip.android.hotel.filter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.filter.FilterTreeNode.Callback;
import ctrip.android.hotel.filter.FilterTreeNode.OnSelectChangeListener;

import java.util.List;

public class FilterTreeView extends ViewGroup implements OnItemClickListener,
		OnSelectChangeListener, Callback {

	private FilterTreeNode mData;
	private FilterTreeView mSubTreeView;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private TreeObjectAdapter mTreeObjectAdapter;
	private Paint paint = new Paint();
	private static final int BG_COLOR_ACTIVIE = Color.parseColor("#ffffff");

	public FilterTreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mListView = new ListView(context);
		mListView.setSelector(R.drawable.hotel_tree_list_selector);
		mTreeObjectAdapter = new TreeObjectAdapter(context);
		mListView.setAdapter(mTreeObjectAdapter);
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
		this.addView(mListView);
		initInfoPanel();
		mListView.setOnItemClickListener(this);
	}

	private void initInfoPanel() {
		mProgressBar = new ProgressBar(getContext());
		mProgressBar.setIndeterminateDrawable(getContext().getResources()
				.getDrawable(R.drawable.loading_drawable_progress));
		mProgressBar.setVisibility(View.GONE);
		this.addView(mProgressBar);
	}

	public void refresh() {
		mTreeObjectAdapter.notifyDataSetChanged();
		this.openSubTree(0);
		if (mSubTreeView != null) {
			mSubTreeView.refresh();
		}
	}

	public void openSubTree(int index) {
		if (index >= mTreeObjectAdapter.getCount()) {
			return;
		}
		FilterTreeNode to = mTreeObjectAdapter.getItem(index);
		if (!to.isLeaf()) {
			if (mSubTreeView == null) {
				mSubTreeView = new FilterTreeView(getContext(), null);
				this.addView(mSubTreeView);
			}
			mSubTreeView.setVisibility(VISIBLE);
			if (to.isLazyLoadMode()) {
				to.setActived(true);
				if (mSubTreeView.mSubTreeView != null) {
					mSubTreeView.mSubTreeView.setVisibility(GONE);
					mSubTreeView.mListView.setVisibility(GONE);
				}
				this.mTreeObjectAdapter.notifyDataSetChanged();
				mSubTreeView.mProgressBar.setVisibility(VISIBLE);
				to.loadSubTreeAsync();
			} else {
				bindSubTree(to);
			}
		}
	}

	private void bindSubTree(FilterTreeNode node) {
		mSubTreeView.setData(node);
		mSubTreeView.mProgressBar.setVisibility(View.GONE);
		node.setActived(true);
		mSubTreeView.setBackgroundColor(Color.WHITE);
		mTreeObjectAdapter.notifyDataSetChanged();

		mSubTreeView.openSubTree(node.getSelectedSubTree());

	}

	public void setData(FilterTreeNode data) {
		mData = data;
		int dividerColor = mData.getDividerColor();
		if (dividerColor == -1) {
			mListView.setDivider(null);
		} else {
			ColorDrawable divider = new ColorDrawable(dividerColor);
			mListView.setDivider(divider);
			mListView.setDividerHeight(DeviceUtil.getPixelFromDip(.5f));
		}
		mListView.setPadding(data.getPadding(), 0, data.getPadding(), 0);
		mListView.setVisibility(VISIBLE);
		for (FilterTreeNode node : data.getSubTrees()) {
			node.setOnSelectChangeListener(this);

			if (node.isLazyLoadMode()) {
				node.setCallback(this);
			}
		}
		mTreeObjectAdapter.setData(data.getSubTrees());
		mListView.setSelection(data.getActivieIndex() - 3);
		requestLayout();
	}

	private class TreeObjectAdapter extends ArrayAdapter<FilterTreeNode> {

		public TreeObjectAdapter(Context context) {
			super(context, -1);
		}

		public void setData(List<FilterTreeNode> data) {
			clear();
			if (data != null) {
				for (FilterTreeNode node : data)
					add(node);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getContext(),
						R.layout.hotel_view_tree_item, null);
			}
			TextView tx = (TextView) convertView.findViewById(R.id.mTreeValue);
			ImageView selectIndicator = (ImageView) convertView
					.findViewById(R.id.mSelectIcon);
			ImageView checkbox = (ImageView) convertView
					.findViewById(R.id.mCheckBox);

			FilterTreeNode node = getItem(position);
			String label = node.getDisplayName();
			int maxLen = mListView.getMeasuredWidth()
					- mListView.getPaddingLeft() - mListView.getPaddingRight()
					- DeviceUtil.getPixelFromDip(21);
			float labelLen = tx.getPaint().measureText(label);
			if (labelLen > maxLen) {
				int lines = (int) ((labelLen + maxLen - 1) / maxLen);
				float lineWith = labelLen / lines;
				int index = tx.getPaint()
						.breakText(label, true, lineWith, null);
				String line0 = label.substring(0, index); // only consider 2
															// line label
				String line1 = label.substring(index, label.length());
				label = line0 + "\n" + line1;
			}
			tx.setText(label);
			int textColor = R.color.hotel_filter_item_text_color;
			LinearLayout container = (LinearLayout) tx.getParent();
			RelativeLayout.LayoutParams param = (android.widget.RelativeLayout.LayoutParams) container
					.getLayoutParams();
			boolean selected = node.isSelected();
			if (!node.isLeaf()) {
				selectIndicator.setVisibility(VISIBLE);
				selectIndicator.setSelected(selected);

				param.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
				tx.setGravity(Gravity.CENTER);
				container.setGravity(Gravity.CENTER);
				tx.setPadding(0, 0, 0, 0);
				if (mData.isRoot()) {
					textColor = R.color.hotel_filter_root_text_color;
				}
				tx.setTextColor(getContext().getResources().getColorStateList(
						textColor));
				tx.setSelected(node.isActive());

				checkbox.setVisibility(View.GONE);
			} else {
				selectIndicator.setVisibility(View.GONE);

				param.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				tx.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				container.setGravity(Gravity.LEFT);
				tx.setPadding(DeviceUtil.getPixelFromDip(11), 0, 0, 0);
				tx.setSelected(selected);

				int checkboxRes = R.drawable.hotel_checkbox_selector_new;
				if (mData.isSingleChoice() || node.isUnlimitedNode()
						|| node.isAllNode()) {
					checkboxRes = R.drawable.hotel_filter_radio_button;
				}
				checkbox.setImageResource(checkboxRes);
				checkbox.setSelected(selected);
			}
			if (node.isActive() && !node.isLeaf()) {
				convertView.setBackgroundColor(BG_COLOR_ACTIVIE);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			tx.setMaxWidth(maxLen);
			int height = mData.getChildHeight();
			if (height > 0) {
				convertView.setMinimumHeight(height);
			}
			return convertView;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		int navWidth = width;
		if (this.mData != null && this.mData.getWidthWeight() != 0) {
			navWidth = (int) (width * this.mData.getWidthWeight());
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

	private void logd(Object msg) {
		Log.d("tree_view", "" + msg);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = this.getMeasuredWidth();
		int height = b - t;
		int navWidth = width;
		if (this.mData != null && this.mData.getWidthWeight() != 0) {
			navWidth = (int) (width * this.mData.getWidthWeight());
		}
		logd("onl: navWidth :" + navWidth);
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
		logd("drawBorder>>> 0");
		if (mListView.getVisibility() != View.VISIBLE) {
			logd("drawBorder>>> return 0");
			return;
		}
		int height = this.getMeasuredHeight();
		int width = mListView.getMeasuredWidth();
		if (mData == null) {
			logd("drawBorder>>> return 1");
			return;
		}
		int selectedTree = mData.getActivieIndex();
		int indexOfSelectedTree = selectedTree
				- mListView.getFirstVisiblePosition();

		View child = mListView.getChildAt(indexOfSelectedTree);
		if (child == null) {
			logd("drawBorder>>> return 2");
			canvas.drawLine(width, 0, width, height, paint);
			return;
		}
		paint.setColor(Color.parseColor("#dddddd"));
		paint.setStrokeWidth(DeviceUtil.getPixelFromDip(0.5f));
		if (mData.isRoot()) {
			logd("drawBorder>>> depth 3");
			canvas.drawLine(width, 0, width, child.getTop(), paint);
			canvas.drawLine(width, child.getBottom(), width, height, paint);
		} else {
			logd("drawBorder>>> depth 2");
			int childHeight = child.getBottom() - child.getTop();
			int shapeHeight = DeviceUtil.getPixelFromDip(10);
			int shapeWidth = DeviceUtil.getPixelFromDip(7);
			int compensation = (childHeight - shapeHeight) / 2;
			canvas.drawLine(width, 0, width, child.getTop() + compensation,
					paint);
			canvas.drawLine(width, child.getTop() + compensation, width
					- shapeWidth, child.getTop() + childHeight / 2, paint);
			canvas.drawLine(width - shapeWidth, child.getTop() + childHeight
					/ 2, width, child.getBottom() - compensation, paint);
			canvas.drawLine(width, child.getBottom() - compensation, width,
					height, paint);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		FilterTreeNode node = mTreeObjectAdapter.getItem(position);
		if (node.getClickHanlder() != null) {
			if (node.getClickHanlder().handleClick(node)) {
				return;
			}
		}
		if (node.isLeaf()) {
			if (node.isSelected()
					&& (node.isUnlimitedNode() || node.isAllNode())) {
				mTreeObjectAdapter.notifyDataSetChanged();
				return;
			}
			if (!node.isSelected() || !mData.isSingleChoice()) {
				node.setSelected(!node.isSelected());
			}
			mTreeObjectAdapter.notifyDataSetChanged();
		} else {
			openSubTree(position);
		}
	}

	@Override
	public void onSelectedChanged(FilterTreeNode node, boolean selected) {
		// TODO Auto-generated method stub
		mTreeObjectAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSubTreesLoaded(FilterTreeNode node) {
		// TODO Auto-generated method stub
		if (node.isActive()) {
			bindSubTree(node);
		}
	}

	@Override
	public void onSubTreeLoadFail(FilterTreeNode node) {
		// TODO Auto-generated method stub
		if (node.isActive() && mSubTreeView != null) {
			mSubTreeView.mProgressBar.setVisibility(View.GONE);
			mSubTreeView.setBackgroundColor(Color.WHITE);
		}
	}

}
*/
