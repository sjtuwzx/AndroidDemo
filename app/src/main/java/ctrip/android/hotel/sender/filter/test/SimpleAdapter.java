package ctrip.android.hotel.sender.filter.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ctrip.android.hotel.sender.filter.FilterNode;

/**
 * Created by wang_zx on 2015/1/7.
 */
public class SimpleAdapter extends ArrayAdapter<FilterNode> {
    private static final String TAG = SimpleAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;

    public SimpleAdapter(Context context) {
        super(context, -1);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<FilterNode> data) {
        clear();
        if (data != null) {
            for (FilterNode item : data)
                add(item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        }
        TextView text = (TextView)convertView;
        text.setText("");

        FilterNode node = getItem(position);
        text.setText(node.getDisplayName());
        text.setTextColor(node.isSelected() ? 0xFF0000FF : 0xFF000000);

        return convertView;
    }

    private static class ViewHolder {
        TextView mText;

    }
}
