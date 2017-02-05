package com.wzx.android.demo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2015/12/3.
 */
public class SubFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = getTargetFragment();
        Toast.makeText(getActivity(), String.format("fragment: %s", fragment == null ? "null" : fragment.toString()), Toast.LENGTH_LONG).show();
       /* Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);*/
    }
}
