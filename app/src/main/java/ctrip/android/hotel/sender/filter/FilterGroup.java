package ctrip.android.hotel.sender.filter;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wang_zx on 2015/1/6.
 * 筛选节点group
 */
public class FilterGroup extends FilterNode implements FilterParent {

    private static final String TAG = FilterGroup.class.getSimpleName();

    protected List<FilterNode> mChildren = new ArrayList<FilterNode>();

    protected String mType;

    //TODO 单选节点下子节点不支持数据关联
    private boolean mSingleChoice = false;

    protected boolean mHasOpened = false;

    private List<FilterNode> mHistorySelectList;

    /**
     * 添加节点
     *
     * @param node 新增节点
     */
    public void addNode(FilterNode node) {
        node.setParent(this);
        mChildren.add(node);
    }

    /**
     * 删除节点
     *
     * @param node 被删除节点
     */
    @Override
    public void remove(FilterNode node) {
        if (mChildren.remove(node)) {
            node.setParent(null);
        }
    }

    /**
     * 获取所有子节点
     *
     * @param needUnlimited 是否包含不限节点
     * @return 所有子节点
     */
    public List<FilterNode> getChildren(boolean needUnlimited) {
        List<FilterNode> children = new ArrayList<FilterNode>(mChildren);
        int N = children.size();
        for (int i = N - 1; i >= 0; i--) {
            FilterNode child = children.get(i);
            if (child instanceof InvisibleFilterNode || (!needUnlimited && child instanceof UnlimitedFilterNode)) {
                children.remove(i);
            }
        }
        return children;
    }

    /**
     * 是否该节点为空
     *
     * @param containUnlimited 是否包含不限节点
     * @return 是否该节点为空
     */
    public boolean isEmpty(boolean containUnlimited) {
        List<FilterNode> children = getChildren(containUnlimited);
        return children.isEmpty();
    }

    /**
     * 设置FilterGroup类型
     *
     * @param type FilterGroup类型
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * 获取FilterGroup类型
     *
     * @return FilterGroup类型
     */
    public String getType() {
        return mType;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    private void clearSelectInvisibleNode() {
        int N = mChildren.size();
        for (int i = N - 1; i >= 0; i--) {
            FilterNode child = mChildren.get(i);
            if (child instanceof  InvisibleFilterNode) {
                child.requestSelect(false);
            } else if (child instanceof FilterGroup) {
                FilterGroup group = (FilterGroup)child;
                group.clearSelectInvisibleNode();
            }
        }
    }

    public void addSelectNode(FilterNode node) {
        if (!contain(node)) {
            InvisibleFilterNode invisibleNode = new InvisibleFilterNode(node);
            dispatchUnknownNode(invisibleNode);
        }
        requestSelect(node, true);
    }

    protected void dispatchUnknownNode(FilterNode node) {
        addNode(node);
    }

    @Override
    public boolean forceSelect(boolean selected) {
        if (mIsSelected && !selected) {
            int N = mChildren.size();
            for (int i = N - 1; i >= 0; i--) {
                FilterNode child = mChildren.get(i);
                if (child instanceof UnlimitedFilterNode) {
                    child.setSelected(true);
                } else {
                    child.forceSelect(false);
                }
            }
        }
        return super.setSelected(selected);
    }

    @Override
    public boolean setSelected(boolean selected) {
        if (mIsSelected && !selected) {
            int N = mChildren.size();
            for (int i = N - 1; i >= 0; i--) {
                FilterNode child = mChildren.get(i);
                if (child instanceof UnlimitedFilterNode) {
                    child.setSelected(true);
                }
            }
        }
        return super.setSelected(selected);

    }

    private List<FilterNode> getTriggerLastChildren(FilterNode trigger) {
        if (contain(trigger, true)) {
            List<FilterNode> children = new ArrayList<FilterNode>(mChildren);
            int N = children.size();
            for (int i = 0; i < N; i++) {
                FilterNode child = children.get(i);
                if (child.contain(trigger, true)) {
                    children.remove(child);
                    children.add(child);
                    break;
                }
            }
            return children;
        } else {
            return mChildren;
        }
    }

    /**
     * 设置为单选节点
     */
    public void setSingleChoice() {
        mSingleChoice = true;
    }

    /**
     * 是否单选
     *
     * @return 是否单选
     */
    public boolean isSingleChoice() {
        return mSingleChoice;
    }

    @Override
    public void requestSelect(FilterNode trigger, boolean selected) {
        if (trigger instanceof UnlimitedFilterNode) {
            if (selected) {
                List<FilterNode> selectedLeafNodes = getSelectedLeafNodes();
                for (FilterNode child : selectedLeafNodes) {
                    child.requestSelect(false);
                }
                trigger.setSelected(true);
                selected = false;
            }
            //clearSelectInvisibleNode();
        } else if (trigger instanceof AllFilterNode && selected) {
            FilterParent triggerParent = trigger.getParent();
            if (triggerParent == this) {
                List<FilterNode> selectedLeafNodes = getSelectedLeafNodes();
                for (FilterNode child : selectedLeafNodes) {
                    child.requestSelect(false);
                }
            }
        }

        FilterParent parent = getParent();
        if (parent != null) {
            parent.requestSelect(trigger, selected);
        }

    }

    @Override
    public boolean refreshSelectState(FilterNode trigger, boolean selected) {
        if (mSingleChoice) {
            List<FilterNode> selectedChildrenList = getSelectedChildren();
            //单选节点中可能包含关联节点，这时以触发节点为主，优先选中
            List<FilterNode> children = getTriggerLastChildren(trigger);
            int N = children.size();
            for (int i = N - 1; i >= 0; i--) {
                FilterNode child = children.get(i);
                boolean newSelected = child.refreshSelectState(trigger, selected);
                //children已排序，正常点击情况下第一个必为reference；关键字联想则取第一个关联节点
                if (newSelected || child.contain(trigger, false)) {
                    if (newSelected && !selectedChildrenList.isEmpty()) {
                        FilterNode node = selectedChildrenList.get(0);
                        //TODO 单选节点下子节点不支持数据关联
                        node.forceSelect(false);
                    }
                    break;
                }
            }
        } else {
            int N = mChildren.size();
            boolean shouldUnSelectAllNode = !(trigger instanceof AllFilterNode) && contain(trigger) ;
            for (int i = N - 1; i >= 0; i--) {
                FilterNode child = mChildren.get(i);
                if (shouldUnSelectAllNode && child instanceof AllFilterNode) {
                    child.setSelected(false);
                } else {
                    child.refreshSelectState(trigger, selected);
                }
            }
        }
        boolean isSelected = mIsSelected;
        int selectedChildrenCount = getSelectedChildrenCount();
        if (selectedChildrenCount > 0) {
            FilterNode unlimitedNode = findUnlimitedNode();
            if (unlimitedNode != null) {
                unlimitedNode.setSelected(false);
            }
            //不必再刷新children状态
            setSelected(true);
        } else {
            setSelected(false);
        }

        return !isSelected && mIsSelected;
    }

    /**
     * 查找“不限”节点
     *
     * @return “不限”节点 or null
     */
    public FilterNode findUnlimitedNode() {
        for (FilterNode child : mChildren) {
            if (child instanceof UnlimitedFilterNode) {
                return child;
            }
        }
        return null;
    }

    /**
     * 获取所有选中子节点
     *
     * @return 所有选中子节点列表
     */
    public List<FilterNode> getSelectedChildren() {
        List<FilterNode> selectedChildrenList = new ArrayList<FilterNode>();
        for (FilterNode child : mChildren) {
            if (!(child instanceof UnlimitedFilterNode) && child.isSelected()) {
                selectedChildrenList.add(child);
            }
        }
        return selectedChildrenList;
    }

    public int getFirstSelectChildPosition(boolean containUnlimited) {
        List<FilterNode> children = getChildren(containUnlimited);
        int N = children.size();
        for (int i = 0; i < N; i++) {
            FilterNode child = children.get(i);
            if (child.isSelected()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取选中子节点个数
     *
     * @return 选中子节点个数
     */
    public int getSelectedChildrenCount() {
        List<FilterNode> selectedChildrenList = getSelectedChildren();
        return selectedChildrenList.size();
    }

    /**
     * 获取下层所有选中叶子节点
     *
     * @return 下层所有选中叶子节点列表
     */
    public List<FilterNode> getSelectedLeafNodes() {
        List<FilterNode> selectLeafNodeList = new ArrayList<FilterNode>();
        for (FilterNode child : mChildren) {
            if (child.isSelected()) {
                if (child instanceof FilterGroup) {
                    FilterGroup group = (FilterGroup) child;
                    List<FilterNode> childSelectLeafNodeList = group.getSelectedLeafNodes();
                    selectLeafNodeList.addAll(childSelectLeafNodeList);
                } else if (!(child instanceof UnlimitedFilterNode)) {
                    selectLeafNodeList.add(child);
                }
            }
        }

        //去除重复节点
        Set<String> selectedCharacterCodes = new HashSet<String>();
        int N = selectLeafNodeList.size();
        for (int i = N - 1; i >= 0; i--) {
            FilterNode node = selectLeafNodeList.get(i);
            String characterCode = node.getCharacterCode();
            if (!TextUtils.isEmpty(characterCode)) {
                if (selectedCharacterCodes.contains(characterCode)) {
                    selectLeafNodeList.remove(i);
                } else {
                    selectedCharacterCodes.add(characterCode);
                }
            }
        }
        return selectLeafNodeList;
    }

    protected void resetFilterGroup() {
        int N = mChildren.size();
        for (int i = N - 1; i >= 0; i--) {
            FilterNode child = mChildren.get(i);
            if (child instanceof FilterGroup) {
                ((FilterGroup) child).resetFilterGroup();
            } else if (child instanceof UnlimitedFilterNode) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
        super.setSelected(false);
    }

    /**
     * 保存当前筛选状态
     */
    public void save() {
        mHistorySelectList = getSelectedLeafNodes();
    }

    /**
     * 恢复先前筛选状态
     */
    public void restore() {
        if (mHistorySelectList != null) {
            forceSelect(false);

            for (FilterNode node : mHistorySelectList) {
                addSelectNode(node);
            }
            discardHistory();
        }
    }

    /**
     * 丢弃先前保存的筛选状态
     */
    public void discardHistory() {
        mHistorySelectList = null;
    }

    /**
     * 是否当前筛选状态与上次save时状态发生改变
     *
     * @return 是否改变
     */
    public boolean hasFilterChanged() {
        if (mHistorySelectList == null) {
            return false;
        }
        Set<String> selectLeafCharacterCodes = new HashSet<String>();
        Set<FilterNode> selectUnknownLeafNodes = new HashSet<FilterNode>();
        for (FilterNode node : mHistorySelectList) {
            String characterCode = node.getCharacterCode();
            if (!TextUtils.isEmpty(characterCode)) {
                selectLeafCharacterCodes.add(characterCode);
            } else {
                selectUnknownLeafNodes.add(node);
            }
        }
        List<FilterNode> selectLeafNodeList = getSelectedLeafNodes();
        for (FilterNode node : selectLeafNodeList) {
            String characterCode = node.getCharacterCode();
            if (!TextUtils.isEmpty(characterCode)) {
                if (!selectLeafCharacterCodes.remove(characterCode)) {
                    return true;
                }
            } else if (!selectUnknownLeafNodes.remove(node)) {
                return true;
            }
        }
        return !selectLeafCharacterCodes.isEmpty() || !selectUnknownLeafNodes.isEmpty();
    }

    /**
     * 根据characterCode判断是否包含指定节点
     *
     * @param node 指定节点
     * @return 是否包含指定节点
     */
    public boolean contain(FilterNode node) {
        return contain(node, false);
    }

    /**
     * 根据characterCode或引用（==）判断是否包含指定节点
     *
     * @param node               指定节点(仅支持叶子节点)
     * @param accordingReference 是否使用引用（==）判断
     * @return 是否包含指定节点
     */
    @Override
    public boolean contain(FilterNode node, boolean accordingReference) {
        String characterCode = node.getCharacterCode();
        if (!accordingReference && TextUtils.isEmpty(characterCode)) {
            return false;
        }
        for (FilterNode child : mChildren) {
            if (child.contain(node, accordingReference)) {
                return true;
            } else if (child == node) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FilterNode findNode(FilterNode node, boolean accordingReference) {
        String characterCode = node.getCharacterCode();
        if (!accordingReference && TextUtils.isEmpty(characterCode)) {
            return null;
        }
        for (FilterNode child : mChildren) {
            FilterNode result = child.findNode(node, accordingReference);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public boolean canOpen() {
        return mFilterGroupOpenPerformer != null;
    }

    public boolean hasOpened() {
        return mHasOpened;
    }

    public synchronized boolean open(FilterGroupOpenListener listener) {
        if (!mHasOpened) {
            if (listener != null) {
                listener.onOpenStart();
            }
            mHasOpened = performOpen(listener);
            if (mHasOpened) {
                save();
                resetFilterGroup();
                restore();
            }
            if (listener != null) {
                listener.onOpenFinish(mHasOpened);
            }
        }
        return mHasOpened;
    }

    protected boolean performOpen(FilterGroupOpenListener listener) {
        if (mFilterGroupOpenPerformer != null) {
            return mFilterGroupOpenPerformer.performOpen(this);
        }
        return false;
    }

    private FilterGroupOpenPerformer mFilterGroupOpenPerformer;

    public void setFilterGroupOpenPerformer(FilterGroupOpenPerformer performer) {
        mFilterGroupOpenPerformer = performer;
    }

    public static interface FilterGroupOpenPerformer {
        boolean performOpen(FilterGroup group);
    }

    public static interface FilterGroupOpenListener {

        void onOpenStart();

        void onOpenSuccess(String filterType);

        void onOpenFail(String filterType);

        void onOpenFinish(boolean success);

    }

}
