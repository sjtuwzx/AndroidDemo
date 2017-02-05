package com.wzx.sticky;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.wzx.sticky.listview.PinnedSingleSectionHeadersListView;

/**
 * Created by wang_zx on 2015/3/11.
 */
public class PinnedHeaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_header);
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
