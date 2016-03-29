package ctrip.android.hotel.sender.filter.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.wzx.android.demo.v2.R;

import java.util.List;

import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;
import ctrip.android.hotel.sender.filter.FilterRoot;
import ctrip.android.hotel.sender.filter.InvisibleFilterNode;

/**
 * Created by wang_zx on 2015/1/7.
 */
public class FilterTestActivity extends Activity implements View.OnClickListener {

    private HorizontalScrollView mSelectListScroll;
    private LinearLayout mSelectListLayout;

    private ListView mListView1;
    private ListView mListView2;
    private ListView mListView3;

    private SimpleAdapter mAdapter1;
    private SimpleAdapter mAdapter2;
    private SimpleAdapter mAdapter3;

    private FilterRoot mFilterRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiter_test);
        mSelectListScroll = (HorizontalScrollView) findViewById(R.id.select_list_scroll);
        mSelectListLayout = (LinearLayout) findViewById(R.id.select_list_layout);

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.restore).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);

        mListView1 = (ListView) findViewById(R.id.list1);
        mAdapter1 = new SimpleAdapter(this);
        mListView1.setAdapter(mAdapter1);
        mListView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView2 = (ListView) findViewById(R.id.list2);
        mAdapter2 = new SimpleAdapter(this);
        mListView2.setAdapter(mAdapter2);

        mListView3 = (ListView) findViewById(R.id.list3);
        mAdapter3 = new SimpleAdapter(this);
        mListView3.setAdapter(mAdapter3);

        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openFilterList(position);
            }
        });

        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterNode node = mAdapter2.getItem(position);
                if (node instanceof FilterGroup) {
                    FilterGroup group = (FilterGroup) node;
                    mAdapter3.setData(group.getChildren(true));
                } else {
                    setNodeSelected(node, !node.isSelected());
                }
            }
        });
        mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterNode node = mAdapter3.getItem(position);
                setNodeSelected(node, !node.isSelected());
            }
        });

        mListView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FilterNode node = mAdapter2.getItem(position);
                if (node instanceof  FilterGroup) {
                    FilterGroup group = (FilterGroup)node;
                    boolean selected = true;
                    List<FilterNode> selectedChildrenList = group.getSelectedChildren();
                    if (!selectedChildrenList.isEmpty()) {
                        FilterNode selectChild = selectedChildrenList.get(0);
                        if (selectChild instanceof InvisibleFilterNode) {
                           selected = false;
                        }
                    }
                    group.requestSelect(selected);
                    mListView2.setItemChecked(position, true);
                    mAdapter3.setData(group.getChildren(true));
                    refreshSelectState();
                }
                return true;
            }
        });

        mFilterRoot = new TestFilterRoot();
        mAdapter1.setData(mFilterRoot.getChildren(true));
        mListView1.setItemChecked(0, true);
        openFilterList(0);
        refreshSelectState();
    }

    private void openFilterList(int position) {
        FilterNode node = mAdapter1.getItem(position);
        if (node instanceof FilterGroup) {
            FilterGroup group = (FilterGroup) node;
            List<FilterNode> children = group.getChildren(true);
            mAdapter2.setData(children);
            FilterNode child = children.get(0);
            if (child instanceof FilterGroup) {
                mListView2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mListView2.setItemChecked(0, true);
                mListView3.setVisibility(View.VISIBLE);
                FilterGroup group1 = (FilterGroup) child;
                mAdapter3.setData(group1.getChildren(true));
            } else {
                mListView2.clearChoices();
                mListView2.setChoiceMode(ListView.CHOICE_MODE_NONE);

                mListView3.setVisibility(View.GONE);
                mAdapter3.setData(null);
            }
        }
    }

    private void refreshSelectState() {
        mAdapter1.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
        mAdapter3.notifyDataSetChanged();
        refreshSelectList();
    }

    private void refreshSelectList() {
        List<FilterNode> selectList = mFilterRoot.getSelectedLeafNodes();
        mSelectListLayout.removeAllViews();
        if (selectList.size() == 0) {
            mSelectListScroll.setVisibility(View.GONE);
        } else {
            mSelectListScroll.setVisibility(View.VISIBLE);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            for (FilterNode node : selectList) {
                Button button = new Button(this);
                button.setText(node.getDisplayName());
                button.setTag(node);
                button.setOnClickListener(mSelectNodeClickListener);
                mSelectListLayout.addView(button, layoutParams);
            }/*
            mSelectListLayout.post(new Runnable() {
                @Override
                public void run() {
                    int offset = mSelectListLayout.getMeasuredWidth() - mSelectListScroll.getWidth();
                    if (offset < 0) {
                        offset = 0;
                    }
                    mSelectListScroll.smoothScrollTo(offset, 0);
                }
            });*/
        }
    }

    private View.OnClickListener mSelectNodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FilterNode node = (FilterNode) v.getTag();
            setNodeSelected(node, false);
        }
    };

    private void setNodeSelected(FilterNode node, boolean selected) {
        node.requestSelect(selected);
        refreshSelectState();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save) {
            mFilterRoot.save();
        } else if (id == R.id.restore) {
            mFilterRoot.restore();
            refreshSelectState();
        } else if (id == R.id.clear) {
            mFilterRoot.resetFilterTree(false);
            refreshSelectState();
        }
    }
}