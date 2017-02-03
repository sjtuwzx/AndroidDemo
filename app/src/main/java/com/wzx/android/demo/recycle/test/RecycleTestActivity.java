package com.wzx.android.demo.recycle.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wzx.android.demo.v2.R;

/**
 * Created by wangzhenxing on 16/7/18.
 */
public class RecycleTestActivity extends Activity {

    private RecycleTestView mRecycleTestView;
    private View mButton;

    private View mItem;
    private Button mItemText;

    private int mIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_test);

        mRecycleTestView = (RecycleTestView) findViewById(R.id.recycler_view);
        mButton = findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecycleTestView.removeChildren();
                refreshText();
                mRecycleTestView.addView(mItem);
            }
        });


    }

    private void refreshText() {
        if (mItem == null) {
            mItem = getLayoutInflater().inflate(R.layout.recycle_test_item, mRecycleTestView, false);
            mItemText = (Button) mItem.findViewById(R.id.item_action_button);
            mItemText.setText(String.format("index[%d]", mIndex));
            mRecycleTestView.addView(mItem);
        } else {
            mItemText.setText(String.format("index[%d]", mIndex));
            /*if (mIndex % 2 == 0) {
                mItemText.requestLayout();
            }*/
        }

        ++mIndex;
    }
}
