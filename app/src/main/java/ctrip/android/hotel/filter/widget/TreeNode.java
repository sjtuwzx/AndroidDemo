package ctrip.android.hotel.filter.widget;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wlzhao,contact me at wlzhao@ctrip.com
 * 
 * <p>
 * TreeNode represents a data node to display in the {@link TreeView}<br>
 * 
 * 
 */
public class TreeNode {
	private Object mValue;
	private TreeNode mParent;
	/* all subtrees */
	private List<TreeNode> mSubTree = new ArrayList<TreeNode>();
	/* contains the seleted sub trees */
	private List<TreeNode> mSeletecdSubTrees = new ArrayList<TreeNode>();
	/* the child node which now is active,namely opened */
	private TreeNode mActiveChildNode;
	private int mMaxSelectCount = -1;
	/* if this node is leaf,mLazyDepth = 0 */
	private int mLazyDepth = -1;
	private Callback mCallback;

	private int mChildHeight = 0;
	private float mChildWidth = 0;
	private int mDividerColor = -1;
	private int mPadding;
	private boolean mSelectionShareWithSibling;
	private boolean mSelectedByInit;
	private boolean mAllowNoSelection;
	private ClickHandler mClickHandler;
	private boolean mIsPreloadNode;
	
	private boolean checkNolimitWhenEmpty = true;
	
	public void setCheckNolimitWhenEmpty(boolean check) {
		checkNolimitWhenEmpty = check;
	}
	
	public boolean canCheckNolimitWhenEmpty() {
		return checkNolimitWhenEmpty;
	}

	public boolean isPreloadNode() {
		return mIsPreloadNode;
	}
	public void setIsPreloadNode(boolean  isPreloadNode) {
		mIsPreloadNode =  isPreloadNode;
	}
	
	public void setAllowNoSelection(boolean allowNoSelection) {
		mAllowNoSelection = allowNoSelection;
	}
	
	public boolean isAllowNoSelection() {
		return mAllowNoSelection;
	}

	public void setSelectedByInit() {
		mSelectedByInit = true;
	}

	public boolean isSelectedByInit() {
		return mSelectedByInit;
	}

	public void setSelectionShareWithSibling(boolean share) {
		mSelectionShareWithSibling = share;
	}

	public boolean isSelectionShareWithSibling() {
		return mSelectionShareWithSibling; 
	}
	/* is this node selectd by user click action */
	private boolean isSeletedByClick;

	public boolean isSelectedByClick() {
		return isSeletedByClick;
	}

	public void setIsSelectedByClick(boolean selectByClick) {
		isSeletedByClick = selectByClick;
	}

	private boolean mTouched = false;
	/* is this node represents " NO LIMITED NODE",then,when it is selected,all siblings should unselected*/
	public boolean isNoLimitNode;
	/* this represents "ALL NODE" */ 
	public boolean isAllNode;
	private String actionCode;

	public void setLogActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public boolean logAction() {
		if (TextUtils.isEmpty(actionCode)) {
			return false;
		}
		return true;
	}

	public void setTouched(boolean touched) {
		mTouched = touched;
	}
	public boolean isTouched() {
		return mTouched;
	}
	public boolean hasSelectedChild() {
		for(TreeNode node : mSeletecdSubTrees) {
			if (!node.isNoLimitNode) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasUnknownSelection() {
		for(TreeNode tn : mSeletecdSubTrees) {
			if (tn.isUnKnowNode()) {
				return true;
			}
		}
		return false;
	}
	
	public int getSelectedSubTree() {
//		if (mSelectedSubTree > 0) {
//			return mSelectedSubTree;
//		}
		if (!mSeletecdSubTrees.isEmpty()) {
			for(TreeNode node : mSeletecdSubTrees) {
				if (!node.isUnKnowNode()) {
					int index =  indexOfSubTree(node);
					if (index != -1) {
						return index;
					}
				}
			};
		}
		if (mSubTreeOpener != null) {
			return mSubTreeOpener.getSubTreeIndex(this);
		}
		return 0;
	}
	
	public int indexOfSubTree(TreeNode node) {
		for(int i=0;i< mSubTree.size();i++) {
			TreeNode sub = mSubTree.get(i);
			if (sub.equals(node)) {
				return i;
			}
		}
		return -1;
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

	/**
	 * 
	 * @param maxSelectCount
	 *            <p>
	 *            maxSelectCount = -1 means no limited
	 *            <p>
	 *            maxSelectCount = 1 means single
	 *            <p>
	 */
	public void setMaxSelectCount(int maxSelectCount) {
		mMaxSelectCount = maxSelectCount;
	}

	public int getMaxSelectCount() {
		return mMaxSelectCount;
	}
	public void setLazyDepth(int depth) {
		mLazyDepth = depth;
	}
	public List<TreeNode> getSubTrees() {
		return mSubTree;
	}

	public static interface Callback {
		public void onSubTreesLoaded(List<TreeNode> trees);
		public void onSubTreeLoadFail();
	}

	public void justCancelLastRequest() {
		if (mLazyLoader != null) {
			mLazyLoader.justCancelLastRequest();
		}
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	public static interface LazyLoader {
		public void lazyLoad(TreeNode node);
		public void justCancelLastRequest();
	}

	public static interface ValueInflater {
		public String inflateLabel(TreeNode node);
		public boolean valueEquals(Object one, Object two);
	}
	
	public static interface ClickHandler {
		public boolean handleClick(TreeNode node);
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
		public void onSelectedChanged(TreeNode node, boolean selected);
	}

	private SubTreeOpener mSubTreeOpener;
	public void setSubTreeOpener(SubTreeOpener onActiveChangeListener) {
		mSubTreeOpener = onActiveChangeListener;
	}
	public static interface SubTreeOpener {
		public int getSubTreeIndex(TreeNode node);
	}

	private ValueInflater mValueInflater;
	public void setValueInflater(ValueInflater inflater) {
		mValueInflater = inflater;
	}

	public String getInflatedLabel() {
		if (mValueInflater == null) {
			return mValue.toString();
		}
		return mValueInflater.inflateLabel(this);
	}

	public boolean isLazyLoadMode() {
		//return mLazyDepth > -1;
		return mLazyDepth > 0;
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

	public boolean isSelected() {
		if (this.mParent == null) {
			return false;
		}
		boolean selected = mParent.mSeletecdSubTrees.contains(this);
		if (selected) {
			return true;
		}
		return false;
	}
	
	public List<TreeNode> getSiblingSelection() {
		List<TreeNode> siblingsSelections = new ArrayList<TreeNode>();
		List<TreeNode> siblings = getSiblings();
		for(TreeNode sibling : siblings) {
			List<TreeNode> siblingSelection = sibling.getSeletedSubTree();
			for(TreeNode selection : siblingSelection)
			if (!siblingsSelections.contains(selection)) {
				siblingsSelections.add(selection);
			}
		}
		return siblingsSelections;
	}

	public void reset() {
		setSelected(false);
		List<TreeNode> selected = this.getSeletedSubTree();
		List<TreeNode> temps = new ArrayList<TreeNode>();
		temps.addAll(selected);
		for(TreeNode node : temps) {
			node.reset();
		}
		selected.clear();
	}

	public void selectChild(TreeNode node) {
		if (!node.isNoLimitNode ||!node.isAllNode ) {
			this.removeNoLimitedSelection();
			this.removeAllNodeSelection();
		}
		if (mSeletecdSubTrees.contains(node)) {
			mSeletecdSubTrees.remove(node);
			mSeletecdSubTrees.add(node);
			return;
		}
		mSeletecdSubTrees.add(node);
		if (node.isNoLimitNode || node.isAllNode) {
			return;
		}
		if (mSelectionShareWithSibling) {
			List<TreeNode> siblings = this.getSiblings();
			for(TreeNode sibling : siblings) {
				TreeNode same = sibling.getEqualChildNode(node);
				if (same != null) {
					same.setSelected(true);
				}
			}
		}
	}
	
	private void removeAll(List<TreeNode> nodes , TreeNode node) {
		ArrayList<TreeNode> temps = new ArrayList<TreeNode>();
		temps.addAll(nodes);
		for(TreeNode temp : temps) {
			if (temp.equals(node)) {
				nodes.remove(temp);
			}
		}
	}
	
	public void removePreloadNode(TreeNode node) {
		for(TreeNode sub : mSeletecdSubTrees) {
			if (sub.equals(node) && sub.isPreloadNode()) {
				mSeletecdSubTrees.remove(sub);
				return;
			}
		}
	}
	
	public void selectNoLimitNode() {
		for(TreeNode node : mSubTree) {
			if (node.isNoLimitNode) {
				mSeletecdSubTrees.add(node);
				break;
			}
		}
	}
	public void deSelectChild(TreeNode node) {
		removeAll(mSeletecdSubTrees,node);
		//node.setTouched(false);
		if (mSeletecdSubTrees.isEmpty()) {
			//selectNoLimitNode();
			setSelected(false);
		}
		if (mSelectionShareWithSibling) {
			List<TreeNode> siblings = this.getSiblings();
			for(TreeNode sibling : siblings) {
				TreeNode same = sibling.getEqualChildNode(node);
				if (same != null) {
					removeAll(sibling.mSeletecdSubTrees,same);
					if (!sibling.hasSelectedChild()) {
						//selectNoLimitNode();
						sibling.setSelected(false);
					}
				}
			}
		}
	}

	private TreeNode getEqualChildNode(TreeNode node) {
		for(TreeNode sub : mSubTree) {
			if (sub.equals(node)) {
				return sub;
			}
		}
		return null;
	}
	
	private boolean mIsUnKnownNode;
	
	public void setIsUnKnownNode(boolean unknownNode) {
		mIsUnKnownNode = unknownNode;
	}
	
	public boolean isUnKnowNode() {
		return mIsUnKnownNode;
	}
	public void setSelected(boolean selected) {
		if (mParent != null) {
			if (!selected) {
				//this.setTouched(false);
				mParent.deSelectChild(this);
			} else {
				if (this.isNoLimitNode || this.isAllNode) {
					ArrayList<TreeNode> temps = new ArrayList<TreeNode>();
					temps.addAll(mParent.mSeletecdSubTrees);
					if (temps.size() == 0) {
						mParent.setSelected(false);
					} else {
						for(int i=0;i<temps.size();i++) {
							TreeNode node = temps.get(i);
							mParent.deSelectChild(node);
						}
					}
				} else {
					mParent.removeNoLimitedSelection();
				}
				boolean canAdd = false;
				if (mParent.mMaxSelectCount == 1) {
					canAdd = true;
					clearSelectionRecursively(mParent,this);
					//mParent.mSeletecdSubTrees.clear();
				} else if (mParent.mMaxSelectCount == -1) {
					canAdd = true;
				} else if (mParent.mSeletecdSubTrees.size() < mParent.mMaxSelectCount - 1) {
					canAdd = true;
				}
				if (canAdd) {
					mParent.selectChild(this);
				}
				if (!this.isNoLimitNode) {
					mParent.setSelected(true);
				}
			}
		}
		if (mOnSelectChangeListener != null) {
			mOnSelectChangeListener.onSelectedChanged(this, selected);
		}
	}
	
	private void clearSelectionRecursively(TreeNode node,TreeNode retain) {
		List<TreeNode> selected = node.getSeletedSubTree();
		List<TreeNode> temps = new ArrayList<TreeNode>();
		temps.addAll(selected);
		for(TreeNode temp : temps) {
			if (retain == temp) {
				continue;
			}
			selected.remove(temp);
			clearSelectionRecursively(temp,null);
		}
	}
	
	private void removeNoLimitedSelection() {
		for(TreeNode node : mSeletecdSubTrees) {
			if (node.isNoLimitNode) {
				mSeletecdSubTrees.remove(node);
				return;
			}
		}
	}
	

	private void removeAllNodeSelection() {
		for(TreeNode node : mSeletecdSubTrees) {
			if (node.isAllNode) {
				mSeletecdSubTrees.remove(node);
				return;
			}
		}
	}

	public boolean isActive() {
		if (mParent == null) {
			return false;
		}
		return this.equals(mParent.mActiveChildNode);
	}

	public void setParent(TreeNode parent) {
		mParent = parent;
	}

	public void setValue(Object value) {
		mValue = value;
	}

	public Object getValue() {
		return mValue;
	}

	public int getCount() {
		return mSubTree.size();
	}

	public void addSubTree(TreeNode subTree) {
		mSubTree.add(subTree);
		subTree.mParent = this;
		if (mActiveChildNode == null) {
			subTree.markActive();
		}
	}

	public TreeNode removeSubTree(TreeNode subTree) {
		mSubTree.remove(subTree);
		subTree.mParent = null;
		return subTree;
	}

	public void removeAllSubTree() {
		for(TreeNode node : mSubTree) {
			node.mParent = null;
		}
		mSubTree.clear();
	}

	public TreeNode getParent() {
		return mParent;
	}

	public TreeNode getRoot() {
		TreeNode root = this;
		TreeNode parent = root.getParent();
		while (parent != null) {
			root = parent;
			parent = root.getParent();
		}
		return root;
	}

	public boolean isRoot() {
		return mParent == null;
	}

	public boolean isLeaf() {
		if (mLazyDepth == -1) {
			return mSubTree.isEmpty();
		} else {
			return mLazyDepth == 0;
		}
	}

	public TreeNode getActivieSubTree() {
		return this.mActiveChildNode;
	}

	public int getActivieIndex() {
		final int defaultIndex = 0;
		if (mActiveChildNode == null) {
			return defaultIndex;
		}
		for(int i=0;i<mSubTree.size();i++) {
			TreeNode node = mSubTree.get(i);
			if (mActiveChildNode.equals(node)) {
				return i;
			}
		}
		return defaultIndex;
	}
	public List<TreeNode> getSiblings() {
		TreeNode parent = getParent();
		List<TreeNode> siblings = new ArrayList<TreeNode>();
		if (getParent() == null) {
			return siblings;
		}
		for (TreeNode child : parent.mSubTree) {
			if (child != this) {
				siblings.add(child);
			}
		}
		return siblings;
	}

	public void markActive() {
		if (mParent == null) {
			return;
		}
		mParent.mActiveChildNode = this;
	}

	public List<TreeNode> getSeletedSubTree() {
		return mSeletecdSubTrees;
	} 
   
	public int getDepth() {
		if (mLazyDepth != -1) {
			return mLazyDepth;
		}
		return getDepth(this);
	}

	public List<TreeNode> getSelectedAndUnSelectedLeavies() {
		List<TreeNode> children = this.mSeletecdSubTrees;
		List<TreeNode> selectedLeaves = new ArrayList<TreeNode>();
		for(TreeNode child : children) {
			if (!child.isTouched() && child.isPreloadNode()) {
				selectedLeaves.add(child);
			}
			if (!child.isLeaf()) {
				List<TreeNode> stn = child.getSelectedAndUnSelectedLeavies();
				selectedLeaves.addAll(stn);
			} else {
				if (child.isSelected() || child.isUnKnowNode()) {
					selectedLeaves.add(child);
				}
			}
		}
		return selectedLeaves;
	}

	public List<TreeNode> getAllLeavies() {
		List<TreeNode> leavies = new ArrayList<TreeNode>();
		if (this.isLeaf()) {
			return  new ArrayList<TreeNode>();
		}
		List<TreeNode> subTrees = this.getSubTrees();
		for (TreeNode node : subTrees) {
			if (node.isLeaf()) {
				leavies.add(node);
			} else {
				List<TreeNode> tl = node.getAllLeavies();
				if (!tl.isEmpty()) {
					leavies.addAll(tl);
				}
			}
		}
		return leavies;
	}

	private static int getDepth(TreeNode to) {
		int max = 0;
		int count = 0;
		for (TreeNode tree : to.mSubTree) {
			if (tree.mLazyDepth != -1) {
				count = tree.mLazyDepth;
			} else if (tree.mSubTree != null && tree.mSubTree.size() > 0) {
				count = getDepth(tree);
			}
			if (count > max) {
				max = count;
			}
			return max + 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "[" + this.isSelected()+"]"+this.getInflatedLabel();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TreeNode)) {
			return false;
		}
		TreeNode that = (TreeNode) obj;
		if (that == this) {
			return true;
		}
		if (this.mValueInflater == null) {
			return false;
		}
		return this.mValueInflater.valueEquals(this.mValue,that.mValue);
	}
}
