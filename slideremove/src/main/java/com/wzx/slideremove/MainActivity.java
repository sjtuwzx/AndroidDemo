package com.wzx.slideremove;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ItemsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.activity_main);
        mAdapter = new ItemsAdapter(this);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(String.format("Item[%d]", i + 1));
        }
        mAdapter.setData(data);
        mListView.setAdapter(mAdapter);
    }
}
