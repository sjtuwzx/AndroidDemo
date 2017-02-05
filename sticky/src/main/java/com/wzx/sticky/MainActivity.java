package com.wzx.sticky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.wzx.sticky.listview.SectionedListAdapter;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private SectionedListAdapter mSectionedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list);
        buildAdapter();
        mListView.setAdapter(mSectionedListAdapter);
    }

    private void buildAdapter() {
        mSectionedListAdapter = new SectionedListAdapter();

        ItemsAdapter itemAdapter = new ItemsAdapter(this);
        for (int i = 0; i < 3; i++) {
            itemAdapter.add(String.format("item[%d]", i + 1));
        }
        addSection(null, itemAdapter);

        MainSectionHeaderCreator mainSectionHeaderCreator = new MainSectionHeaderCreator(this);
        SectionedListAdapter.SectionInfo mainSectionInfo = new SectionedListAdapter.SectionInfo.Builder()
                .setHeaderCreator(mainSectionHeaderCreator)
                .pinnedDoubleHeader()
                .build();
        mSectionedListAdapter.addSection(mainSectionInfo);

        for (int i = 0; i < 10; i++) {
            HeaderSectionCreator headerSectionCreator = new HeaderSectionCreator(this, i + 1);
            ItemsAdapter adapter = new ItemsAdapter(this);
            for (int j = 0; j <= i; j++) {
                adapter.add(String.format("item[%d]", j + 1));
            }

            SectionedListAdapter.SectionInfo sectionInfo = new SectionedListAdapter.SectionInfo.Builder()
                    .setHeaderCreator(headerSectionCreator)
                    .setAdapter(adapter)
                    .pinnedHeader()
                    .build();
            mSectionedListAdapter.addSection(sectionInfo);
        }
    }

    private void addSection(SectionedListAdapter.SectionInfo.HeaderCreator headerCreator, BaseAdapter adapter) {
        SectionedListAdapter.SectionInfo sectionInfo = new SectionedListAdapter.SectionInfo.Builder()
                .setHeaderCreator(headerCreator)
                .setAdapter(adapter)
                .build();
        mSectionedListAdapter.addSection(sectionInfo);
    }
}
