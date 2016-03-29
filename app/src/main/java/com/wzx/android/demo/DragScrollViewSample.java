package com.wzx.android.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/5/19.
 */
public class DragScrollViewSample extends Activity {

    private DragScrollView mDragScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_scroll_view);
        mDragScrollView = (DragScrollView)findViewById(R.id.scroll_view);
    }
}
