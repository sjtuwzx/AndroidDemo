package com.wzx.android.demo.scrolllayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

public class ScrollLinearLayoutActivity extends Activity {

    private Button mButton;
    private ListView mListView;
    private TestAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("wzx", "onCreate");
        setContentView(R.layout.activity_scroll_linear_layout);

        Log.i("wzx", "setContentView");
        mButton = (Button) findViewById(R.id.btn);
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new TestAdapter(this);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(String.format("Item[%d]", i + 1));
        }
        mAdapter.setData(data);
        mListView.setAdapter(mAdapter);
    }
}
