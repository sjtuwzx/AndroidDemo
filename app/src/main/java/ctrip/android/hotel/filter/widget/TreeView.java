package ctrip.android.hotel.filter.widget;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.filter.DeviceInfoUtil;

import java.util.ArrayList;
import java.util.List;


public class TreeView extends ViewGroup {

	private TreeNode mData;
	private TreeView mSubTreeView;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private TextView mErrorMessage;
	private TreeObjectAdapter mTreeObjectAdapter = new TreeObjectAdapter();
	private int mDepth = 1;
	private Paint paint = new Paint();
	private static final int BG_COLOR_ACTIVIE = Color.parseColor("#ffffff");

	public void refresh() {
		mTreeObjectAdapter.notifyDataSetChanged();
		this.openSubTree(0);
		if (mSubTreeView != null) {
			mSubTreeView.refresh();
		}
	}

	private void initInfoPanel() {
		mProgressBar = new ProgressBar(getContext());
		mProgressBar.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.loading_drawable_progress));
		mErrorMessage = new TextView(getContext());
		mProgressBar.setVisibility(View.GONE);
		mErrorMessage.setVisibility(View.GONE);
		this.addView(mProgressBar);
	}

	public void showProgressBar(boolean show) {
		mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}
	private class TreeObjectAdapter extends BaseAdapter {
		private List<TreeNode> data = new ArrayList<TreeNode>();

		public void setData(List<TreeNode> data) {
			this.data = data;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getContext(), R.layout.hotel_view_tree_item, null);
			}
			TextView tx = (TextView) convertView.findViewById(R.id.mTreeValue);
			ImageView selectIndicator = (ImageView) convertView.findViewById(R.id.mSelectIcon);
			ImageView checkbox = (ImageView) convertView.findViewById(R.id.mCheckBox);
			TreeNode obj = data.get(position);
			TreeNode parentNode = obj.getParent();
			String label = obj.getInflatedLabel().trim();
			int maxLen = mListView.getMeasuredWidth() - mListView.getPaddingLeft() - mListView.getPaddingRight() - DeviceInfoUtil.getPixelFromDip(21);
			float labelLen = tx.getPaint().measureText(label);
			if (labelLen > maxLen) {
				int lines = (int)((labelLen + maxLen - 1) / maxLen);
				float lineWith = labelLen / lines;
				int index = tx.getPaint().breakText(label, true, lineWith, null);
				String line0 = label.substring(0,index); // only consider 2 line label
				String line1 = label.substring(index, label.length());
				label = line0 + "\n" + line1;
			}
			tx.setText(label);
			tx.setGravity(Gravity.CENTER);
			int textColor = R.color.hotel_filter_item_text_color;
			boolean selected = obj.hasSelectedChild() || obj.isSelected() || (obj.isNoLimitNode && parentNode.getSeletedSubTree().isEmpty() && parentNode.canCheckNolimitWhenEmpty());
			if (!obj.isLeaf()) {
				checkbox.setVisibility(View.GONE);
				if (parentNode.isRoot()) {
					textColor = R.color.hotel_filter_root_text_color;
				}
				tx.setTextColor(getContext().getResources().getColorStateList(textColor));
				tx.setSelected(obj.isActive());
				selectIndicator.setVisibility(VISIBLE);
				selectIndicator.setSelected(selected);
			} else if (obj.isLeaf()) {
				selectIndicator.setVisibility(View.GONE);
				LinearLayout container = (LinearLayout)tx.getParent();
				RelativeLayout.LayoutParams param = (android.widget.RelativeLayout.LayoutParams) container.getLayoutParams();
				param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
				param.width= RelativeLayout.LayoutParams.WRAP_CONTENT;
				tx.setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
				container.setGravity(Gravity.LEFT);
				tx.setPadding(DeviceInfoUtil.getPixelFromDip(11), 0, 0, 0);
				int checkboxRes = R.drawable.hotel_checkbox_selector_new;
				if (parentNode.getMaxSelectCount() == 1 || obj.isNoLimitNode || obj.isAllNode) {
					checkboxRes = R.drawable.hotel_filter_radio_button;
				}
				checkbox.setImageResource(checkboxRes);
				checkbox.setSelected(selected);
				tx.setSelected(selected);
			} 
			if (obj.isActive() && ! obj.isLeaf()) {
				convertView.setBackgroundColor(BG_COLOR_ACTIVIE);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			tx.setMaxWidth(maxLen);
			int height = obj.getParent().getChildHeight();
			if (height > 0) {
				convertView.setMinimumHeight(height);
			}
			return convertView;
		}
	}

	public void setData(TreeNode data) {
		if (mData != data) {
			this.removeView(mSubTreeView);
			mSubTreeView = null;
		}
		mData = data;
		mDepth = data.getDepth();
		int dividerColor = mData.getDividerColor();
		if (dividerColor == -1) {
			mListView.setDivider(null);
		} else {
			ColorDrawable divider = new ColorDrawable(dividerColor);
			mListView.setDivider(divider);
			mListView.setDividerHeight(DeviceInfoUtil.getPixelFromDip(.5f));
		}
		mListView.setPadding(data.getPadding(), 0, data.getPadding(), 0);
		mListView.setVisibility(VISIBLE);

		if (mDepth >= 0 && mSubTreeView == null) {
			mSubTreeView = new TreeView(getContext(), null);
			this.addView(mSubTreeView);
			TreeNode subTree = mData.getActivieSubTree();
			if (subTree != null) {
				mSubTreeView.setData(subTree);
			}
		}
		mSubTreeView.setVisibility(VISIBLE);
		for (final TreeNode node : data.getSubTrees()) {
			node.setOnSelectChangeListener(new TreeNode.OnSelectChangeListener() {
				@Override
				public void onSelectedChanged(TreeNode node, boolean selected) {
					mTreeObjectAdapter.notifyDataSetChanged();
				}
			});
			if (node.isLazyLoadMode()) {
				node.setCallback(new TreeNode.Callback() {
					@Override
					public void onSubTreesLoaded(List<TreeNode> trees) {
						bindSubTree(node, mListView);
					}
					@Override
					public void onSubTreeLoadFail() {
						mSubTreeView.mProgressBar.setVisibility(View.GONE);
						mSubTreeView.setBackgroundColor(Color.WHITE);
					}
				});
			}
		}
		mListView.setAdapter(mTreeObjectAdapter);
		mTreeObjectAdapter.setData(data.getSubTrees()); 
		mListView.setSelection(data.getActivieIndex() - 3);
		this.requestLayout();
	}

	private void bindSubTree(TreeNode node, AdapterView<?> parent) {
		mSubTreeView.setData(node);
		mSubTreeView.mProgressBar.setVisibility(View.GONE);
		node.markActive();
		mSubTreeView.setBackgroundColor(Color.WHITE);
		if (parent != null) {
			((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
		}
		mSubTreeView.openSubTree(node.getSelectedSubTree());
	}

	public TreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mListView = new ListView(context);
		mListView.setSelector(R.drawable.hotel_tree_list_selector);
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
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, View view,
					int position, long id) {
				final TreeNode node = (TreeNode) mListView.getItemAtPosition(position);
				@SuppressWarnings("unused")
				boolean loged = node.logAction();
				if (node.getClickHanlder() != null) {
					if (node.getClickHanlder().handleClick(node)) {
						return;
					}
				}
				node.setTouched(true);
				int depth = node.getParent().getDepth();

				if (node.isLeaf()) {
					if (node.isSelected() && (node.isNoLimitNode || node.isAllNode)){
						mTreeObjectAdapter.notifyDataSetChanged();
						return;
					}
					if (!node.isSelected() || node.getParent().getMaxSelectCount() != 1) {
						node.setSelected(!node.isSelected());
					}
					mTreeObjectAdapter.notifyDataSetChanged();
				} else {
					openSubTree(position);
				}
			}
		});
	}

	public void openSubTree(int index) {
		if (index >= mListView.getCount()) {
			return;
		}
		final TreeNode to = (TreeNode) mListView.getItemAtPosition(index);
		if (to.isLazyLoadMode()) {
			to.markActive();
			if (mSubTreeView.mSubTreeView != null) {
				mSubTreeView.mSubTreeView.setVisibility(GONE);
				mSubTreeView.mListView.setVisibility(GONE);
			}
			this.mTreeObjectAdapter.notifyDataSetChanged();
			mSubTreeView.mProgressBar.setVisibility(VISIBLE);
			if (to.getSubTrees().size() <= 1) {
				to.loadSubTreeAsync();
			} else {
				to.justCancelLastRequest();
				bindSubTree(to, mListView);
			}
		} else {
			bindSubTree(to, mListView);
		}
	}

	public int getCount() {
		return mData.getCount();
	}

	public void setSubTreeView(TreeView treeView) {
		mSubTreeView = treeView;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		int depth = mDepth;
		if (depth < 1) {
			depth = 1;
		}
		int navWidth = width / depth;
		if (this.mData != null && this.mData.getWidthWeight() != 0) {
			navWidth = (int) (width * this.mData.getWidthWeight());
		}
		int childWidth = width - navWidth;
		logd("onm: navWidth :" + navWidth + ",depth:" + depth);
		if (mListView != null) {
			int widthSpec = MeasureSpec.makeMeasureSpec(navWidth,
                    MeasureSpec.EXACTLY);
			int heightSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
			mListView.measure(widthSpec, heightSpec);
		}
		
		int pgWidthSpec =  MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
		int pgHeightSpec =  MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
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
		int level = mDepth;
		if (level < 1) {
			level = 1;
		}
		int navWidth = width / level;
		if (this.mData != null && this.mData.getWidthWeight() != 0) {
			navWidth = (int) (width * this.mData.getWidthWeight());
		}
		logd("onl: navWidth :" + navWidth + ",level:" + level);
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
		mProgressBar.layout(mStartX, startY, mStartX+mProgressBarWidth, startY+mProgressBarHeight);
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
		int indexOfSelectedTree = selectedTree - mListView.getFirstVisiblePosition();

		View child = mListView.getChildAt(indexOfSelectedTree);
		if (child == null) {
			logd("drawBorder>>> return 2");
			canvas.drawLine(width, 0, width, height, paint);
			return;
		}
		paint.setColor(Color.parseColor("#dddddd"));
		paint.setStrokeWidth(DeviceInfoUtil.getPixelFromDip(0.5f));
		if (mData.getDepth() == mData.getRoot().getDepth()) {
			logd("drawBorder>>> depth 3");
			canvas.drawLine(width, 0, width, child.getTop(), paint);
			canvas.drawLine(width,child.getBottom(), width, height, paint);
		} else if (mData.getDepth() > 1){
			logd("drawBorder>>> depth 2");
			int childHeight = child.getBottom() - child.getTop();
			int shapeHeight = DeviceInfoUtil.getPixelFromDip(10);
			int shapeWidth = DeviceInfoUtil.getPixelFromDip(7);
			int compensation = (childHeight - shapeHeight) / 2 ;
			canvas.drawLine(width, 0, width, child.getTop()+compensation, paint);
			canvas.drawLine(width, child.getTop()+compensation, width-shapeWidth, child.getTop()+childHeight/2, paint);
			canvas.drawLine(width-shapeWidth, child.getTop()+childHeight/2, width, child.getBottom()-compensation, paint);
			canvas.drawLine(width,child.getBottom()-compensation, width, height, paint);
		} else {
			logd("drawBorder>>> else");
		}
	}
}
