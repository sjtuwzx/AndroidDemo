package com.wzx.android.demo.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/12/3.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String tag = MainFragment.class.getSimpleName();
        FragmentManager fm = getFragmentManager();
        Fragment f = fm.findFragmentByTag(tag);
        if (f != null) {
            return;
        }
        Fragment fragment = new SubFragment();
        fragment.setTargetFragment(this, 0);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, fragment, tag);
        ft.addToBackStack(tag);
        ft.commit();
    }
}
