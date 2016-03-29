package com.wzx.android.demo.label;

import java.util.HashMap;

/**
 * 营销标签展示 ViewModel
 * Created by sfzhang on 2015/3/31.
 */
public class HotelTagViewModel extends ViewModel {
    // 标签类型
    public int tagType = 0;

    // 标签ID
    public int tagId = 0;

    //标签位置
    public int tagPosition;

    // 是否有副标题
    public boolean hasSubTitle = false;

    // 样式 ViewModel
    public HotelTagStyleViewModel styleViewModel = new HotelTagStyleViewModel();

    // 立即确认、代理 ICON 值
    public int imageIcon = -1;


    //华人标签的url
    public String imageHuaIcon = "";

    // tagDescription是否可以折叠
    public boolean collaspisble;

    // 默认样式 map
    private static HashMap<Integer,HashMap<String, String>> defaultTagMap = new HashMap<Integer,HashMap<String, String>>();


}