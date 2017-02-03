package com.wzx.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wzx.android.demo.adapter.InsertItemAdapter;
import com.wzx.android.demo.pinnedHeader.ItemsAdapter;
import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhenxing on 16/10/12.
 */

public class ItemInsertAdapterSample extends Activity {

    private ListView mListView;
    private InsertItemAdapter mInsertItemAdapter;
    private ItemsAdapter mItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_insert_adapter);
        TextView textView = (TextView) findViewById(R.id.text);
        String text = "费用 \n\r" +
                "以下费用和押金由酒店在提供服务、办理入住或退房手续时收取。 \n" +
                "客房内高速有线上网费用：每 24 小时 AUD20（价格可能有所波动） \n" +
                "公共区域无线上网费用：每 24 小时 AUD15（价格可能有所波动）  \n" +
                "公共区域高速有线上网费用：每 24 小时 AUD15（价格可能有所波动） \n" +
                "机场接送费：每人 AUD36（往返）  \n" +
                "自助泊车费用：每晚 AUD20         \n" +
                "婴儿床费用：每晚 AUD30\n" +
                "折叠床使用费：每晚 AUD 30   \n" +
                "上面所列内容可能并不完整。这些费用和押金可能不包括税款，并且可能会随时发生变化。";
        textView.setText(Html.fromHtml(text));

        final ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(R.drawable.hotel_top_current);
            }
        });

        mListView = (ListView) findViewById(R.id.list);
        mItemsAdapter = new ItemsAdapter(this);
        mInsertItemAdapter = new InsertItemAdapter(mItemsAdapter);
        mListView.setAdapter(mInsertItemAdapter);

        List<String> data = new ArrayList<String>();
        int N =  30;
        for (int i = 0; i < N; i++) {
            data.add(String.format("%d - %d", 1, i + 1));
        }
        mItemsAdapter.setData(data);

        for (int i = 1; i <= 30; i++) {
            mInsertItemAdapter.addInsertItem(i * 2, new InsertedItemCreatorV1(i));
        }

        for (int i = 1; i <= 30; i++) {
            mInsertItemAdapter.addInsertItem(i * 3, new InsertedItemCreatorV2(i));
        }

    }

    private class InsertedItemCreatorV1 implements InsertItemAdapter.InsertedItemCreator {

        private int mIndex;

        public InsertedItemCreatorV1(int index) {
            mIndex = index;
        }

        @Override
        public View getView(View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.insert_item_v1, parent, false);
            }
            Button button = (Button) convertView.findViewById(R.id.btn);
            button.setText(String.format("Inserted Item[%d]", mIndex));
            return convertView;
        }
    }

    private class InsertedItemCreatorV2 implements InsertItemAdapter.InsertedItemCreator {

        private int mIndex;

        public InsertedItemCreatorV2(int index) {
            mIndex = index;
        }

        @Override
        public View getView(View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.inserted_item_v2, parent, false);
            }
            Button button = (Button) convertView.findViewById(R.id.btn1);
            button.setText(String.format("Inserted Item[%d]", mIndex));
            return convertView;
        }
    }
}
