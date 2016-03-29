package com.wzx.android.demo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by wang_zx on 2015/12/3.
 */
public class HotelFragmentTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Fragment mainFragment = new MainFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, mainFragment, "main");
            ft.commitAllowingStateLoss();
        }

    }
}
