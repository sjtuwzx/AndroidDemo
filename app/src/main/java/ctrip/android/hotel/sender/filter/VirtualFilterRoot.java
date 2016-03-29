package ctrip.android.hotel.sender.filter;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wang_zx on 2015/1/29.
 */
public class VirtualFilterRoot extends FilterRoot {

    private Map<String, FilterNode> mChildrenMap = new HashMap<String, FilterNode>();

    @Override
    public void addNode(FilterNode node) {
        mChildren.add(node);
        String type = ((FilterGroup)node).getType();
        if (!TextUtils.isEmpty(type)) {
            mChildrenMap.put(type, node);
        }
    }

    @Override
    public FilterNode getChild(String type) {
        return mChildrenMap.get(type);
    }
}
