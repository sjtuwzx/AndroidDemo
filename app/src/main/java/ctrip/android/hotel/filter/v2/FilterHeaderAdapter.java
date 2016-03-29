package ctrip.android.hotel.filter.v2;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

import java.util.List;

import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;

/**
 * Created by wang_zx on 2015/12/30.
 */
public class FilterHeaderAdapter extends ArrayAdapter<FilterGroup> {

    private LayoutInflater mInflater;

    public FilterHeaderAdapter(Context context) {
        super(context, View.NO_ID);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFilterGroup(FilterGroup group) {
        clear();
        if (group != null) {
            List<FilterNode> children = group.getChildren(false);
            for (FilterNode child : children) {
                if (child instanceof FilterGroup) {
                    add((FilterGroup)child);
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.hotel_filter_section_header_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FilterGroup group = getItem(position);
       // convertView.setSelected(group.isSelected());
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) viewHolder.textLayout
                .getLayoutParams();
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        viewHolder.textView.setGravity(Gravity.CENTER);
        viewHolder.textLayout.setGravity(Gravity.CENTER);
        viewHolder.textView.setPadding(0, 0, 0, 0);

        viewHolder.selectIcon.setSelected(group.isSelected());
        viewHolder.textView.setSelected(group.isSelected());
        viewHolder.textView.setText(group.getDisplayName());

        return convertView;

    }

    private static class  ViewHolder {

        private LinearLayout textLayout;
        private TextView textView;
        private View selectIcon;

        public ViewHolder(View view) {
            textLayout = (LinearLayout) view.findViewById(R.id.item_name_layout);
            textView = (TextView) view.findViewById(R.id.item_name);
            selectIcon = view.findViewById(R.id.select_icon);
        }
    }
}
