package com.wzx.android.demo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.wzx.android.demo.pinnedHeader.PinnedSingleSectionHeadersListView;
import com.wzx.android.demo.pinnedHeader.SingleSectionAdapter;
import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/3/11.
 */
public class PinnedHeaderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_header);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        PinnedSingleSectionHeadersListView listView = (PinnedSingleSectionHeadersListView)findViewById(R.id.list);
        SingleSectionAdapter adapter = new SingleSectionAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("wzx", "onItemClick: " + position);
            }
        });
    }
}
