package ctrip.android.hotel.filter.widget;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.sender.filter.FilterRoot;
import ctrip.android.hotel.sender.filter.test.TestFilterRoot;

import android.app.Activity;
import android.os.Bundle;

public class FilterActivity extends Activity {
	
	private FilterListView mFilterHeaderListView;
	private FilterListAdapter mHeaderAdapter;
	
	private FilterListView mFilterMiddleListView;
	private FilterListAdapter mMiddleAdapter;
	
	
	private FilterListView mFilterTailListView;
	private FilterListAdapter mTailAdapter;
	
	
	private FilterRoot mFilterRoot = new TestFilterRoot();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		mFilterHeaderListView = (FilterListView) findViewById(R.id.filter_header_list);
		mHeaderAdapter = new FilterListAdapter(this);
		mFilterHeaderListView.setAdapter(mHeaderAdapter);		
		mHeaderAdapter.setFilterGroup(mFilterRoot);
		
		mFilterMiddleListView = (FilterListView) findViewById(R.id.filter_middle_list);
		mMiddleAdapter = new FilterListAdapter(this);
		mMiddleAdapter.setMode(FilterListView.MODE_MIDDLE);
		mFilterMiddleListView.setAdapter(mMiddleAdapter);		
		mMiddleAdapter.setFilterGroup(mFilterRoot);
		
		mFilterTailListView = (FilterListView) findViewById(R.id.filter_tail_list);
		mTailAdapter = new FilterListAdapter(this);
		mTailAdapter.setMode(FilterListView.MODE_TAIL);
		mFilterTailListView.setAdapter(mTailAdapter);
		
		mTailAdapter.setFilterGroup(mFilterRoot);
	}

}
