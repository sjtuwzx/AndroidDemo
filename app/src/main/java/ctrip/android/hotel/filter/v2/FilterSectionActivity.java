package ctrip.android.hotel.filter.v2;

import android.app.Activity;
import android.os.Bundle;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.sender.filter.FilterGroup;
import ctrip.android.hotel.sender.filter.test.TestFilterRoot;

/**
 * Created by wang_zx on 2015/12/30.
 */
public class FilterSectionActivity extends Activity {

    private FilterSectionView mFilterSectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_section);

        mFilterSectionView = (FilterSectionView) findViewById(R.id.filter_section_view);
        FilterGroup group = new TestFilterRoot();
        mFilterSectionView.openFilterGroup(group);
    }
}
