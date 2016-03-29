package ctrip.android.hotel.filter;

import java.util.List;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.filter.FilterTreeView.LazyLoader;
import ctrip.android.hotel.filter.FilterTreeView.TreeViewConfig;
import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;
import ctrip.android.hotel.sender.filter.FilterRoot;
import ctrip.android.hotel.sender.filter.test.TestFilterRoot;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class FilterTreeActivity extends Activity {

	private FilterTreeView mFilterTreeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_tree);
		mFilterTreeView = (FilterTreeView) findViewById(R.id.filter_tree);
        mFilterTreeView.setContainUnlimitedNode(false);
        mFilterTreeView.setIndicateSelectState(true);
		mFilterTreeView.setLazyLoader(mLazyLoader);
		mFilterTreeView.setOnItemClickListener(new FilterTreeView.OnItemClickListener() {
            @Override
            public void onLeafItemClick(FilterTreeView treeView, View view, FilterGroup parent, FilterNode node, int position) {
                node.requestSelect(!node.isSelected());
                mFilterTreeView.refresh();
            }

            @Override
            public void onGroupItemClick(FilterTreeView treeView, View view, FilterGroup parent, FilterGroup group, int position) {
                treeView.openSubTree(position);
            }
        });
		TestFilterRoot root = new TestFilterRoot();
		bindViewConfig(root);

		mFilterTreeView.setFilterGroup(root);
	}

	private LazyLoader mLazyLoader = new LazyLoader() {

		@Override
		public void lazyLoad(FilterTreeView treeView, FilterGroup group,
				int position) {
			// TODO Auto-generated method stub

			new Thread(new OpenTreeTask(treeView, group, position)).start();

		}

	};

	private class OpenTreeTask implements Runnable {
		private FilterTreeView mFilterTreeView;
		private FilterGroup mFilterGroup;
		private int mPosition;

		public OpenTreeTask(FilterTreeView treeView, FilterGroup group,
				int position) {
			mFilterTreeView = treeView;
			mFilterGroup = group;
			mPosition = position;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			final boolean result = mFilterGroup.open(null);
			if (result) {
				bindViewConfig(mFilterGroup);
			}

			runOnUiThread(new Runnable() {
				public void run() {
					if (result) {
						mFilterTreeView.onSubTreeLoadSuccess(mFilterGroup,
								mPosition);
					} else {
						mFilterTreeView.onSubTreeLoadFail(mFilterGroup,
								mPosition);
					}

				}
			});

		}
	}

	private void bindViewConfig(FilterGroup group) {
		TreeViewConfig config = new TreeViewConfig();
		group.setTag(config);

		List<FilterNode> children = group.getChildren(true);
		int N = children.size();
		for (int i = 0; i < N; i++) {
			FilterNode child = children.get(i);
			if (i == 0) {
				if (child.isLeaf()) {
					config.dividerColor = Color.parseColor("#dddddd");
					config.itemMinHeight = 120;
					config.padding = 10;
				} else if (group instanceof FilterRoot) {
					config.dividerColor = Color.parseColor("#dddddd");
					config.widthWeight = 0.28f;
					config.itemMinHeight = 120;
				} else {
					config.widthWeight = 0.4f;
					config.itemMinHeight = 120;
					config.padding = 10;
				}
			}
			if (child instanceof FilterGroup) {
				FilterGroup childGroup = (FilterGroup) child;
				bindViewConfig(childGroup);
			}
		}

	}

}
