package com.wzx.sticky.scrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.wzx.sticky.R;

public class StickyScrollActivity extends AppCompatActivity {

    private ObservableScrollView mScrollView;
    private StickyScrollDescendantView mInsideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_scroll);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mInsideView = (StickyScrollDescendantView) findViewById(R.id.scroll_inside_view);
        mScrollView.setOnScrollChangeListener(new ObservableScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(View v, int scrollX, int scrollY) {
                mInsideView.scroll();
            }
        });


        mInsideView.addStickyView(findViewById(R.id.layout2), findViewById(R.id.btn4));
        mInsideView.addStickyView(findViewById(R.id.btn5), findViewById(R.id.btn7));
        mInsideView.addStickyView(findViewById(R.id.btn7), findViewById(R.id.btn8));

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("wzx", "Button2 onClick");
                ((ViewGroup)findViewById(R.id.btn5).getParent()).setVisibility(View.VISIBLE);
            }
        });
    }
}
