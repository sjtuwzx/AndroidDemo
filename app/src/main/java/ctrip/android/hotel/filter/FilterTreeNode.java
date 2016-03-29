package ctrip.android.hotel.filter;/*package ctrip.android.hotel.filter;


import java.util.ArrayList;
import java.util.List;

import ctrip.android.hotel.sender.filter.AllFilterNode;
import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;
import ctrip.android.hotel.sender.filter.FilterRoot;
import ctrip.android.hotel.sender.filter.UnlimitedFilterNode;


*//**
 * @author wlzhao,contact me at wlzhao@ctrip.com
 * 
 * <p>
 * TreeNode represents a data node to display in the {@link TreeView}<br>
 * 
 * 
 *//*
public class FilterTreeNode {
	private FilterTreeNode mParent;
	 all subtrees 
	private List<FilterTreeNode> mSubTree = new ArrayList<FilterTreeNode>();
	 the child node which now is active,namely opened 
	private boolean mIsActived = false;
	
	private Callback mCallback;

	private int mChildHeight = 0;
	private float mChildWidth = 0;
	private int mDividerColor = -1;
	private int mPadding;
	private ClickHandler mClickHandler;
	
	private FilterNode mFilterNode;
	
	private boolean mNeedUnlimited = true;
	
	public FilterTreeNode(FilterNode node) {
		mFilterNode = node;
	}
	
	public FilterNode getFilterNode() {
		return mFilterNode;
	}
	
	public String getDisplayName() {
		return mFilterNode.getDisplayName();
	}
	
	public void setSelected(boolean selected) {
		mFilterNode.requestSelect(selected);
	}

	public boolean isRoot() {
		return mFilterNode instanceof FilterRoot;
	}
	
	public boolean isLeaf() {
		return mFilterNode.isLeaf();
	}
	
	public boolean isSingleChoice() {
		if (mFilterNode instanceof FilterGroup) {
			FilterGroup group = (FilterGroup)mFilterNode;
			return group.isSingleChoice();
		}
		return false;
	}
	
	public boolean isLazyLoadMode() {
		if (mFilterNode instanceof FilterGroup) {
			FilterGroup group = (FilterGroup)mFilterNode;
			return group.canOpen() && !group.hasOpened();
		}
		return false;
	}
	
	private LazyLoader mLazyLoader;

	public void setLazyLoader(LazyLoader lazyLoader) {
		mLazyLoader = lazyLoader;
	}
	public void loadSubTreeAsync() {
		if (mLazyLoader != null) {
			mLazyLoader.lazyLoad(this);
		}
	}
	
	public boolean isUnlimitedNode() {
		return mFilterNode instanceof UnlimitedFilterNode;
	}
	
	public boolean isAllNode() {
		return mFilterNode instanceof AllFilterNode;
	}
	
	public int getSelectedSubTree() {
		if (mFilterNode instanceof FilterGroup) {
			FilterGroup group = (FilterGroup)mFilterNode;
			List<FilterNode> children = group.getChildren(mNeedUnlimited);
			int N = children.size();
			for (int i = 0; i < N; i++) {
				FilterNode child = children.get(i);
				if (child.isSelected()) {
					return i;
				}
			}
		}
		return 0;
	}

	public void setPadding(int padding) {
		mPadding = padding; 
	}

	public int getPadding() {
		return mPadding;
	}

	public void setDividerColor(int color) {
		mDividerColor = color;
	}

	public int getDividerColor() {
		return mDividerColor;
	}

	public void setChildHeight(int childHeight) {
		mChildHeight = childHeight;
	}

	public void setWidthWeight(float widthWeight) {
		mChildWidth = widthWeight;
	}

	public int getChildHeight() {
		return mChildHeight;
	}
	public float getWidthWeight() {
		return mChildWidth;
	}

	public Callback getCallback() {
		return mCallback;
	}
	
	public List<FilterTreeNode> getSubTrees() {
		return mSubTree;
	}

	

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	public static interface LazyLoader {
		public void lazyLoad(FilterTreeNode node);
		public void justCancelLastRequest();
	}
	
	public static interface ClickHandler {
		public boolean handleClick(FilterTreeNode node);
	}
	
	public void setClickHandler(ClickHandler handler) {
		mClickHandler = handler;
	}
	
	public ClickHandler getClickHanlder() {
		return mClickHandler;
	}
	
	private OnSelectChangeListener mOnSelectChangeListener;

	public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
		mOnSelectChangeListener = onSelectChangeListener;
	}

	public static interface OnSelectChangeListener {
		public void onSelectedChanged(FilterTreeNode node, boolean selected);
	}


	public boolean isSelected() {
		return mFilterNode.isSelected();
	}
	
	public void clearChildrenActiveState() {
		for (FilterTreeNode child : mSubTree) {
			child.setActived(false);
		}
	}
	
	public void setActived(boolean actived) {
		if (actived && mParent != null) {
			mParent.clearChildrenActiveState();
		} else if (!actived) {
			clearChildrenActiveState();
		}
		mIsActived = actived;
	}

	public boolean isActive() {
		if (isRoot()) {
			return true;
		} else if (mParent == null) {
			return false;
		}
		return mIsActived && mParent.isActive();
	}
	
	public int getActivieIndex() {
		int N = mSubTree.size();
		for (int i = 0; i < N; i++){
			FilterTreeNode child = mSubTree.get(i);
			if (child.isActive()) {
				return i;
			}
		} 
		return -1;
	}

	public void addSubTree(FilterTreeNode subTree) {
		mSubTree.add(subTree);
		subTree.mParent = this;
	}
}
*/