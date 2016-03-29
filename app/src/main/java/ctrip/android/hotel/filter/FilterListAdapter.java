package ctrip.android.hotel.filter;

import java.util.List;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.filter.FilterTreeView.TreeViewConfig;
import ctrip.android.hotel.sender.filter.AllFilterNode;
import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;
import ctrip.android.hotel.sender.filter.FilterRoot;
import ctrip.android.hotel.sender.filter.UnlimitedFilterNode;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilterListAdapter extends ArrayAdapter<FilterNode> {

	private static final int BG_COLOR_ACTIVIE = Color.parseColor("#ffffff");
	
	private FilterGroup mFilterGroup;
	private TreeViewConfig mTreeViewConfig;
	
	private int mActivePosition = -1;

    private boolean mShowSelectInIndicator = true;

	public FilterListAdapter(Context context) {
		super(context, -1);
		// TODO Auto-generated constructor stub
	}
	
	public void setActivePosition(int position) {
		mActivePosition = position;
		notifyDataSetChanged();
	}
	
	public int getActivePosition() {
		return mActivePosition;
	}
	
	public void setFilterGroup(FilterGroup group, boolean showUnlimitedNode, boolean showSelectInIndicator) {
		if (mFilterGroup != group) {
			mActivePosition = -1;
		}
		mFilterGroup = group;
        mShowSelectInIndicator = showSelectInIndicator;
		clear();
		if (mFilterGroup != null) {
			mTreeViewConfig = (TreeViewConfig)mFilterGroup.getTag();		
			List<FilterNode> children = group.getChildren(showUnlimitedNode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                addAll(children);
            } else if (children != null) {
                for (FilterNode child : children) {
                    add(child);
                }
            }
		}
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(getContext(),
					R.layout.hotel_view_tree_item, null);
		}
		TextView tx = (TextView) convertView.findViewById(R.id.mTreeValue);
		ImageView selectIndicator = (ImageView) convertView
				.findViewById(R.id.mSelectIcon);
		ImageView checkbox = (ImageView) convertView
				.findViewById(R.id.mCheckBox);

		FilterNode node = getItem(position);
		String label = node.getDisplayName();
		int maxLen = parent.getMeasuredWidth()
				- parent.getPaddingLeft() - parent.getPaddingRight()
				- DeviceInfoUtil.getPixelFromDip(21);
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
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) container
				.getLayoutParams();
		boolean selected = node.isSelected();
		if (!node.isLeaf()) {
            if (mShowSelectInIndicator) {
                selectIndicator.setVisibility(View.VISIBLE);
                selectIndicator.setSelected(selected);
            }

			param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			tx.setGravity(Gravity.CENTER);
			container.setGravity(Gravity.CENTER);
			tx.setPadding(0, 0, 0, 0);
			if (mFilterGroup instanceof FilterRoot) {
				textColor = R.color.hotel_filter_root_text_color;
			}
			tx.setTextColor(getContext().getResources().getColorStateList(
					textColor));
			tx.setSelected(position == mActivePosition);

			checkbox.setVisibility(View.GONE);
		} else {
			selectIndicator.setVisibility(View.GONE);

			param.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			tx.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			container.setGravity(Gravity.LEFT);
			tx.setPadding(DeviceInfoUtil.getPixelFromDip(11), 0, 0, 0);
			tx.setSelected(selected);

            if (mShowSelectInIndicator) {
                checkbox.setVisibility(View.VISIBLE);
                int checkboxRes = R.drawable.hotel_checkbox_selector_new;
                if ((mFilterGroup != null && mFilterGroup.isSingleChoice()) || node instanceof UnlimitedFilterNode
                        || node instanceof AllFilterNode) {
                    checkboxRes = R.drawable.hotel_filter_radio_button;
                }
                checkbox.setImageResource(checkboxRes);
                checkbox.setSelected(selected);
            }
		}
		if (position == mActivePosition && !node.isLeaf()) {
			convertView.setBackgroundColor(BG_COLOR_ACTIVIE);
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		tx.setMaxWidth(maxLen);
		
		int height = mTreeViewConfig.itemMinHeight;
		if (height > 0) {
			convertView.setMinimumHeight(height);
		}
		return convertView;
	}

}
