package ctrip.android.hotel.filter.v2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wzx.android.demo.pinnedHeader.SectionedListAdapter;
import com.wzx.android.demo.pinnedHeader.SectionedListCommonAdapter;
import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

import ctrip.android.hotel.filter.DeviceInfoUtil;
import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.FilterNode;

/**
 * Created by wang_zx on 2015/12/29.
 */
public class FilterSectionView extends ViewGroup {

    private static final float HEADER_LIST_WIDTH_FACTOR = 0.3f;

    private LayoutInflater mInflater;
    private ListView mHeaderListView;
    private ListView mSectionListView;
    private FilterHeaderAdapter mHeaderAdapter;
    private SectionedListCommonAdapter mSectionAdapter;


    private FilterGroup mFilterGroup;

    private int mHeaderListWidth;
    private int mBorderWidth;
    private Paint mPaint = new Paint();

    public FilterSectionView(Context context) {
        this(context, null);
    }

    public FilterSectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterSectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FilterSectionView, defStyleAttr, 0);
        mHeaderListWidth = a.getDimensionPixelSize(R.styleable.FilterSectionView_header_list_width, 0);
        a.recycle();

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mHeaderListView = makeAndAddListView(context);
        mHeaderAdapter = new FilterHeaderAdapter(context);
        mHeaderListView.setAdapter(mHeaderAdapter);

        mSectionListView = makeAndAddListView(context);
        mSectionAdapter = new SectionedListCommonAdapter();
        mSectionListView.setAdapter(mSectionAdapter);

        mBorderWidth = DeviceInfoUtil.getPixelFromDip(0.5f);
        mPaint.setColor(0xFFDDDDDD);
        mPaint.setStrokeWidth(mBorderWidth);
    }

    private ListView makeAndAddListView(Context context) {
        ListView listView = new ListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDividerHeight(0);
        addView(listView);

        return listView;
    }

    public void openFilterGroup(FilterGroup group) {
        mFilterGroup = group;
        mHeaderAdapter.setFilterGroup(group);
        buildFilterSection(group);
    }

    private void buildFilterSection(FilterGroup group) {
        mSectionAdapter.removeAllAdapterInfo();
        List<FilterNode> children = group.getChildren(false);
        for (FilterNode child : children) {
            if (child instanceof FilterGroup) {
                FilterGroup childGroup = (FilterGroup)child;
                FilterSectionItemAdapter adapter = new FilterSectionItemAdapter(getContext());
                SectionedListAdapter.AdapterInfo adapterInfo = new SectionedListAdapter.AdapterInfo.Builder()
                        .setHeaderCreator(new FilterSectionHeaderCreator(mInflater, childGroup.getDisplayName()))
                        .setAdapter(adapter).create();

                ArrayList groupList = new ArrayList();
                groupList.add(childGroup);
                adapter.setData(groupList);
                mSectionAdapter.addAdapterInfo(adapterInfo);
            }
        }
    }

    private static class FilterSectionHeaderCreator implements SectionedListAdapter.AdapterInfo.HeaderCreator {

        private LayoutInflater mInflater;
        private String mTitle;

        public FilterSectionHeaderCreator(LayoutInflater inflater, String title) {
            mInflater = inflater;
            mTitle = title;
        }

        @Override
        public boolean hasHeader() {
            return true;
        }

        @Override
        public View onHeaderCreate(View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.hotel_view_tree_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setTextColor(0xff099fde);
            viewHolder.textView.setText(mTitle);

            return convertView;

        }

        private static class  ViewHolder {
            TextView textView;

            public ViewHolder(View view) {
                textView = (TextView) view.findViewById(R.id.mTreeValue);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int sectionListWidth = width - mHeaderListWidth;
        measureChildListView(mHeaderListView, mHeaderListWidth, height);
        measureChildListView(mSectionListView, sectionListWidth, height);

    }

    private void measureChildListView(View child, int width, int height) {
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildListView(mHeaderListView, 0);
        layoutChildListView(mSectionListView, mHeaderListView.getMeasuredWidth());
    }

    private void layoutChildListView(View child, int offsetX) {
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        child.layout(offsetX, 0, width + offsetX, height);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        int height = getHeight();
        int width = mHeaderListView.getWidth();
        canvas.drawLine(width, 0, width, height, mPaint);
    }

    public interface OnItemClickListener {
        void onLeafItemClick(FilterSectionView sectionView, View view,
                             FilterGroup parent, FilterNode node, int position);

        void onGroupItemClick(FilterSectionView sectionView, View view,
                              FilterGroup parent, FilterGroup group, int position);
    }

}
