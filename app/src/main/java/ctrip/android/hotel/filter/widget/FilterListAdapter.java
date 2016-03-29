package ctrip.android.hotel.filter.widget;

import java.util.List;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.filter.DeviceInfoUtil;
import ctrip.android.hotel.filter.widget.FilterListView.FilterAdapter;
import ctrip.android.hotel.sender.filter.AllFilterNode;
import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;
import ctrip.android.hotel.sender.filter.UnlimitedFilterNode;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilterListAdapter extends ArrayAdapter<FilterNode> implements FilterAdapter{

	private final LayoutInflater mInflater;	
	
	private int mMode = FilterListView.MODE_HEADER;
	
	private int mAcitivePosition = -1;
	
	private FilterGroup mFilterGroup;
	

	private static final int BG_COLOR_ACTIVIE = Color.parseColor("#ffffff");

	public FilterListAdapter(Context context) {
		super(context, -1);
		// TODO Auto-generated constructor stub
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setFilterGroup(FilterGroup group) {
		mFilterGroup = group;
		List<FilterNode> children = group.getChildren(true);
		clear();
		addAll(children);
	}
	
	public void setMode(int mode) {
		mMode = mode;
	}

	@Override
	public int getMode() {
		// TODO Auto-generated method stub
		return mMode;
	}

	@Override
	public int getActiviePosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(getContext(), R.layout.hotel_view_tree_item, null);
		}
		TextView tx = (TextView) convertView.findViewById(R.id.mTreeValue);
		ImageView selectIndicator = (ImageView) convertView.findViewById(R.id.mSelectIcon);
		ImageView checkbox = (ImageView) convertView.findViewById(R.id.mCheckBox);
		
		FilterNode node = getItem(position);
		String label = node.getDisplayName();
		int maxLen = parent.getMeasuredWidth() - parent.getPaddingLeft() - parent.getPaddingRight() - DeviceInfoUtil.getPixelFromDip(21);
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
		boolean selected = node.isSelected();
		if (!node.isLeaf()) {
			checkbox.setVisibility(View.GONE);
			if (mMode == FilterListView.MODE_HEADER) {
				textColor = R.color.hotel_filter_root_text_color;
			}
			tx.setTextColor(getContext().getResources().getColorStateList(textColor));
			tx.setSelected(mAcitivePosition == position);
			selectIndicator.setVisibility(View.VISIBLE);
			selectIndicator.setSelected(selected);
		} else if (node.isLeaf()) {
			selectIndicator.setVisibility(View.GONE);
			LinearLayout container = (LinearLayout)tx.getParent();
			RelativeLayout.LayoutParams param = (android.widget.RelativeLayout.LayoutParams) container.getLayoutParams();
			param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			param.width= RelativeLayout.LayoutParams.WRAP_CONTENT;
			tx.setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
			container.setGravity(Gravity.LEFT);
			tx.setPadding(DeviceInfoUtil.getPixelFromDip(11), 0, 0, 0);
			int checkboxRes = R.drawable.hotel_checkbox_selector_new;
			if ((mFilterGroup != null && mFilterGroup.isSingleChoice()) || node instanceof UnlimitedFilterNode || node instanceof AllFilterNode) {
				checkboxRes = R.drawable.hotel_filter_radio_button;
			}
			checkbox.setImageResource(checkboxRes);
			checkbox.setSelected(selected);
			tx.setSelected(selected);
		} 
		if (position == mAcitivePosition && ! node.isLeaf()) {
			convertView.setBackgroundColor(BG_COLOR_ACTIVIE);
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		tx.setMaxWidth(maxLen);
		return convertView;
	}

}
