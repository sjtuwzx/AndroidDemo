package com.wzx.android.demo.label;

/**
 * 营销标签样式 ViewModel
 * Created by sfzhang on 2015/3/31.
 */
public class HotelTagStyleViewModel extends ViewModel {
    // 主标签 ViewModel
    public HotelTagBasicViewModel mainTagViewModel = new HotelTagBasicViewModel();

    // 副标签 ViewModel
    public HotelTagBasicViewModel subTagViewModel = new HotelTagBasicViewModel();

    // 标签说明，房型浮层使用
    public String tagDescription = "";

    // 边框宽度，像素值
    public float tagFrameWidth = 0.0f;

    // 边框圆角，像素值
    public float tagCornerRadius = 0.0f;

    // 边框颜色 #argb
    public String tagFrameColor = "";

    @Override
    public Object clone() {
        HotelTagStyleViewModel cloneObject = null;
        try {
            cloneObject = (HotelTagStyleViewModel)super.clone();
            cloneObject.mainTagViewModel = (HotelTagBasicViewModel)this.mainTagViewModel.clone();
            cloneObject.subTagViewModel = (HotelTagBasicViewModel)this.subTagViewModel.clone();
        } catch (Exception e) {
            ctrip.business.privateClass.BusinessLogUtil.d("Exception", e);
        }
        return cloneObject;
    }
}