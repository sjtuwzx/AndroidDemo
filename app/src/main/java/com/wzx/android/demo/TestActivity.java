package com.wzx.android.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

import ctrip.android.hotel.HotelInvoicePostDialog;

/**
 * Created by wang_zx on 2014/12/29.
 */
public class TestActivity extends FragmentActivity{

    private static final String TAG = TestActivity.class.getSimpleName();

    private static final String COMMENT_CONTENT = "本次入住的是商务大床房，378/天，一共3天。\n地理位置距离市中心和外滩都不算远，坐公交车大约三、四站地，打车20元左右。酒店对面就有一家快客，旁边还有一间水果店，买点日用品零食都很方便。酒店东边一条小路有多家美发足浴的店，但都是从事色情服务的，晚上经过时要小心。\n酒店前台的服务人员态度都很不错，办理任何事情都很迅速。因为我和老公都不吸烟，就把我们安排在11层，11层是无烟层。\n房间布置基本与携程网上的图片一致。设施一应俱全，布置的时尚温馨。各种设施都非常新，卫生间宽敞明亮，冰柜、吹风机都有。11层是顶楼，视野也很不错。\n唯一不足的是，房间内感觉灰尘稍多了一些。好在每天服务员都会打扫。床上用品和洗手间的用品也都更换的很勤。\n酒店还有免费的WIFI，虽然速度时快时慢。\n总之，这是一次很不错的酒店体验。个人很喜欢。虽然价格比连锁酒店稍微贵了一点，但各方面都要更高档更舒适。";

    private static final String CONTENT =
            "习主席军队的一次重要会议上讲：今后军官的3/" +
                    "收入主要是靠工资，不能有其他所谓的679灰色收入，更不能有违法所得，否则就要受到查处和追究。这段话是讲给部队听的，也是讲给全党同志特别是领导干部听的。廉政建设做到了这一点，“不能”才算真正见到成效；党员干部自觉做到这一点，“不想”才算真正成为“新常态”。";
    private static final String SHORT_CONTENT = "习近平：军官收入靠工资 有灰色收入要查处";

    private ExpandableTextView mTextView;
    private CollapsibleTextView mCollapsibleTextView;
    private int mPosition = 0;

    private HotelLocationTextView mHotelLocationTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mHotelLocationTextView = (HotelLocationTextView)findViewById(R.id.location_text);
        mHotelLocationTextView.setPositionDistanceFromText("距上海火车站色收入要查处");
        mHotelLocationTextView.setDistanceText("11.2公里");
        mHotelLocationTextView.setCommercialDistrictText("人民广场地区火车站色收入要查处");
       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
           getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/

        /*mTextView = (ExpandableTextView)findViewById(R.id.text);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setExpanded(!mTextView.isExpanded());
            }
        });
        mCollapsibleTextView = (CollapsibleTextView) findViewById(R.id.text1);
        mCollapsibleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCollapsibleTextView.setCollapsed(!mCollapsibleTextView.isCollapsed());
            }
        });
        onClick(null);*/
    }


    public static class TabManager implements HotelTabsLayout.OnSelectedChangeListener {
        private final FragmentActivity mActivity;
        private final int mContainerId;
        private final SparseArrayCompat<TabInfo> mTabs = new SparseArrayCompat<TabInfo>();

        TabInfo mLastTab;

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;
            private final String tag;
            private Fragment fragment;

            TabInfo(Class<?> _class, Bundle _args, String _tag) {
                clss = _class;
                args = _args;
                tag = _tag;
            }
        }

        public TabManager(FragmentActivity activity, int containerId) {
            mActivity = activity;
            mContainerId = containerId;
        }

        public void addTab(Class<?> clss, Bundle args, String tag) {

            TabInfo info = new TabInfo(clss, args, tag);

            info.fragment = mActivity.getSupportFragmentManager()
                    .findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isHidden()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager()
                        .beginTransaction();
                ft.hide(info.fragment);
                ft.commitAllowingStateLoss();
            }

            mTabs.put(mTabs.size(), info);
        }

        public Fragment getCurrentFragment() {
            if (mLastTab == null)
                return null;
            return mLastTab.fragment;
        }

        @Override
        public void onSelectedChange(HotelTabsLayout layout, int selectedIndex) {
            TabInfo newTab = mTabs.get(selectedIndex);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager()
                        .beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.hide(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.show(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commitAllowingStateLoss();
                mActivity.getSupportFragmentManager()
                        .executePendingTransactions();
            }
        }
    }

}