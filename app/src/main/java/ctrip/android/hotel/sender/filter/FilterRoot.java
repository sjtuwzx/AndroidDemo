package ctrip.android.hotel.sender.filter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wang_zx on 2015/1/6.
 * 筛选树root
 */
public class FilterRoot extends FilterGroup {

    private Map<String, FilterNode> mChildrenMap = new HashMap<String, FilterNode>();

    @Override
    public void addNode(FilterNode node) {
        super.addNode(node);
        if (node instanceof FilterGroup) {
        	FilterGroup group = (FilterGroup)node;
            String type = group.getType();
            if (!TextUtils.isEmpty(type)) {
                mChildrenMap.put(type, node);
            }
        }
    }

    /**
     * 根据type获取节点
     * @param type 节点类型
     * @return 符合type的节点 or null
     */
    public FilterNode getChild(String type) {
        return mChildrenMap.get(type);
    }

    /**
     * 从指定位置获取子节点列表
     * @param start 起始位置
     * @return 子节点列表
     */
    public List<FilterNode> getChildrenFromPosition(int start) {
        if (start == 0) {
            return mChildren;
        }  else {
            List<FilterNode> result = new ArrayList<FilterNode>();
            for (int i = start; i < mChildren.size(); i++) {
                result.add(mChildren.get(i));
            }
            return result;
        }
    }

    @Override
    public void requestSelect(FilterNode trigger, boolean selected) {
        if (!(trigger instanceof UnlimitedFilterNode)) {
            refreshSelectState(trigger, selected);
        }
    }

    /**
     * 清空所有选中状态
     */
    public void resetFilterTree(boolean force) {
        if (force) {
            resetFilterGroup();
        } else {
            forceSelect(false);
        }
    }

    /**
     * 保存当前筛选状态
     */
    @Override
    public void save() {
        for (FilterNode child : mChildren) {
            FilterGroup group = (FilterGroup)child;
            group.save();
        }
    }

    /**
     * 恢复先前筛选状态
     */
    @Override
    public void restore() {
        for (FilterNode child : mChildren) {
            FilterGroup group = (FilterGroup)child;
            group.restore();
        }
    }

    /**
     * 丢弃先前保存的筛选状态
     */
    @Override
    public void discardHistory() {
        for (FilterNode child : mChildren) {
            FilterGroup group = (FilterGroup)child;
            group.discardHistory();
        }
    }

    /**
     * 是否当前筛选状态与上次save时状态发生改变
     * @return 是否改变
     */
    @Override
    public boolean hasFilterChanged() {
        for (FilterNode child : mChildren) {
            FilterGroup group = (FilterGroup)child;
            if (group.hasFilterChanged()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean open(FilterGroupOpenListener listener) {
        if (listener != null) {
            listener.onOpenStart();
        }
        if (!mHasOpened) {
            boolean hasOpened = true;
            for (FilterNode child : mChildren) {
                if (child instanceof FilterGroup) {
                    FilterGroup group = (FilterGroup) child;
                    if (group.canOpen() && !group.hasOpened()) {
                        boolean result = group.open(null);
                        if (listener != null) {
                            if (result) {
                                listener.onOpenSuccess(group.getType());
                            } else {
                                listener.onOpenFail(group.getType());
                            }
                        }
                        hasOpened &= result;
                    }
                }
            }
            mHasOpened = hasOpened;
        }
        if (listener != null) {
            listener.onOpenFinish(mHasOpened);
        }
        return mHasOpened;
    }
}
