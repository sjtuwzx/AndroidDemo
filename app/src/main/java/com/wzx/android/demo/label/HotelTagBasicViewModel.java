package com.wzx.android.demo.label;

/**
 * 营销标签基础类
 * Created by sfzhang on 2015/3/31.
 */
public class HotelTagBasicViewModel extends ViewModel {
    /** 标签 title */
    public String tagTitle = "";

    /** 标签字体大小 */
    public float tagFontSize = 0.0f;

    /** 标签字体颜色, 格式：#argb */
    public String tagFontColor = "";

    /** 标签背景色, 格式：#argb */
    public String tagBackgroundColor = "";

    @Override
    public Object clone() {
        HotelTagBasicViewModel cloneObject = null;
        try {
            cloneObject = (HotelTagBasicViewModel)super.clone();
        } catch (Exception e) {
            ctrip.business.privateClass.BusinessLogUtil.d("Exception", e);
        }
        return cloneObject;
    }
}