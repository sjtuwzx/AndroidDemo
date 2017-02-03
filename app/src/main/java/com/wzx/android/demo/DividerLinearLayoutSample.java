package com.wzx.android.demo;

import android.app.Activity;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.widget.ImageView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/7/7.
 */
public class DividerLinearLayoutSample extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divider_linear_layout);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        ColorFilter colorFilter = new PorterDuffColorFilter(0xffff0000, PorterDuff.Mode.SRC_ATOP);
        imageView.setColorFilter(colorFilter);
    }
}
