package com.wzx.android.demo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wzx.android.demo.pinnedHeader.HotelDemoAutoCollapseAdapter;
import com.wzx.android.demo.pinnedHeader.ItemsAdapter;
import com.wzx.android.demo.pinnedHeader.SectionedListAdapter.AdapterInfo;
import com.wzx.android.demo.pinnedHeader.PinnedDragableHeaderListView;
import com.wzx.android.demo.pinnedHeader.SectionedListCommonAdapter;
import com.wzx.android.demo.v2.R;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	private PinnedDragableHeaderListView mListView;
    private SectionedListCommonAdapter mAdapter;

    private int mIndex = 0;
	

	private byte[] readFile(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int readBytes = 0;
		while ((readBytes = in.read(buffer)) > 0) {
			out.write(buffer, 0, readBytes);
		}

		
		return out.toByteArray();
	}

    public static Context sContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sContext = this;

        StringBuilder sb = new StringBuilder();
        sb.append('a').append('b').append('c');

        String ab = "ab";
        String s1 = "a" + "b" + "c";
        String s2 = "a" + "b" + "c";
        String s3 = "ab" + "c";
        String s4 = ab + "c";
        String s5 = sb.toString() + "c";

        System.out.println("===========================================================");
        System.out.println(s1 == s2);
        System.out.println(s1 == s3);
        System.out.println(s1 == s4);
        System.out.println(s1 == s5);
        System.out.println(s1 == sb.toString());
        System.out.println(sb.toString());
        System.out.println("===========================================================");


        TextView textView = (TextView) findViewById(R.id.text);
        Drawable drawable = getResources().getDrawable(R.drawable.hotel_icon_comment_c);
        drawable.setBounds(0, 0, 178, 78);
        ImageSpan imgSpan = new ImageSpan(drawable);
        SpannableString spanString = new SpannableString("中新网4月27日电 据央视报道，a韩国国务总理郑烘原于当地时间27日上午召开发布会，称自己应对韩国“岁月号”沉船事件负责，宣布辞职，并希望家属能原谅及理解他的决定。");
        spanString.setSpan(imgSpan, 30, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanString);

        HotelFilterLayout filterLayout = (HotelFilterLayout)findViewById(R.id.filter_layout);
        filterLayout.setPreferenceView(findViewById(R.id.btn_preference));

		mListView = (PinnedDragableHeaderListView) findViewById(R.id.list);
        mListView.setDividerHeight(0);

        mAdapter = new SectionedListCommonAdapter();
        AdapterInfo adapterInfo = new AdapterInfo.Builder().setHeaderCreator(new DoublePinnedHeaderCreator()).pinnedDoubleHeader().create();
        mAdapter.addAdapterInfo(adapterInfo);

        for (int i = 0; i < 5; i++) {
            addSection(i);
        }

        TextView text = new TextView(this);
        text.setGravity(Gravity.CENTER);
        text.setText("header");
        text.setHeight(500);
        mListView.addHeaderView(text);

        text = new TextView(this);
        text.setHeight(2000);
        text.setGravity(Gravity.CENTER);
        text.setText("footer");
        mListView.addFooterView(text);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.first_header, mListView, false);
        //header.setBackgroundColor(0xFFFFFF00);
        //mListView.addHeaderView(header);
       /* mListView.setMainPinnedHeader(header, 1, mListView.getHeaderViewsCount() + mAdapter.getCount() - mListView
                .getFooterViewsCount());*/
       /* mListView.setMainPinnedHeader(header, 1, mListView.getHeaderViewsCount() + mAdapter.getCount() - mListView
                .getFooterViewsCount());*/

        mListView.setAdapter(mAdapter);

        findViewById(R.id.image).setBackground(new HotelBorderDrawable(Color.RED));
        findViewById(R.id.image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //shack(v);
                Intent intent = new Intent(MainActivity.this, AnimationListViewActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_preference).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewParent parent = v.getParent();
                while (parent != null) {
                    Log.i("wzx", parent.toString());
                    parent = parent.getParent();
                }
            }
        });

    }

    private class DoublePinnedHeaderCreator implements AdapterInfo.HeaderCreator, OnClickListener, ObservableHorizontalScrollView.OnScrollChangeListener {

        private int mIndex = 0;

        private int mScrollX = 0;

        @Override
        public boolean hasHeader() {
            return true;
        }

        @Override
        public View onHeaderCreate(View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.scroll_header, parent, false);


            }
            ObservableHorizontalScrollView scrollView = (ObservableHorizontalScrollView)convertView.findViewById(R.id.scroll_view);
            scrollView.setScrollX(mScrollX);
            scrollView.setOnScrollChangeListener(this);

            return convertView;
        }

        @Override
        public void onClick(View v) {
            ++mIndex;
            Button btn = (Button)v;
            btn.setText(String.format("[%d]", mIndex + 1));
        }

        @Override
        public void onScrollChanged(View v, int scrollX, int scrollY) {
            mListView.invalidate();
            mScrollX = scrollX;
        }
    }

    private void shack(View v) {
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() / 2);

        List<Animator> animators = new ArrayList<Animator>();
        animators.add(createOnceShakeAnimator(v));

        ValueAnimator animator = ValueAnimator.ofInt(0);
        animator.setDuration(300);
        animators.add(animator);

        animators.add(createOnceShakeAnimator(v));

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animators);

        animSet.start();
    }

    private Animator createOnceShakeAnimator(View v) {
        List<Animator> animators = new ArrayList<Animator>();
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", -10f, 10f);
        animator.setDuration(100);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(6);
        animators.add(animator);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(v, "rotation", 0f);
        animator3.setDuration(0);
        animators.add(animator3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animators);

        return animatorSet;
    }


    private void addSection(int index) {
        ItemsAdapter adapter = new ItemsAdapter(this);
        List<String> data = new ArrayList<String>();
        int N =  10 * (index + 1);
        for (int i = 0; i < N; i++) {
            data.add(String.format("%d - %d", index + 1, i + 1));
        }
        adapter.setData(data);
        AdapterInfo adapterInfo = new AdapterInfo.Builder()
                .setAdapter(new HotelDemoAutoCollapseAdapter(this, mAdapter, adapter, 5, false)).setHeaderCreator(new SimpleHeaderCreator(index)).setShouldPinHeader(true).create();

        mAdapter.addAdapterInfo(adapterInfo);
    }

    private class SimpleHeaderCreator implements AdapterInfo.HeaderCreator, OnClickListener {

        private int mSection = 0;

        public SimpleHeaderCreator(int section) {
            mSection = section;
        }

        @Override
        public boolean hasHeader() {
            return true;
        }

        @Override
        public View onHeaderCreate(View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.category_header,
                        parent, false);
            }
            Button btn = (Button) convertView.findViewById(R.id.btn3);
            btn.setText(String.format("[%d]", mSection + 1));

            convertView.setOnClickListener(this);

            return convertView;
        }

        @Override
        public void onClick(View v) {
            AdapterInfo adapterInfo = mAdapter.getAdapterInfo(mSection);
            adapterInfo.setIsExpanded(!adapterInfo.isExpanded());

            mAdapter.notifyDataSetChanged();
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub


            /*mListView.setMainPinnedHeaderRegion(1, mListView.getHeaderViewsCount() + mAdapter.getCount() - mListView
                .getFooterViewsCount());
            if (!adapterInfo.mIsExpanded) {
                //int pinnedHeight = mListView.getDragableHeaderVisibleHeight();
                int pinnedHeight = mListView.getMainPinnedVisibleHeight();
                int top = v.getTop();
                if (top < pinnedHeight) {
                    int section = mAdapter.getSection(adapterInfo);
                    int position = mAdapter.getSectionFirstPosition(section);
                    mListView.setSelectionFromTop(position + mListView.getHeaderViewsCount(),
                            pinnedHeight);// + (section == 0 ? 0 :v.getHeight() / 6));
                }
            }*/


	}
}
