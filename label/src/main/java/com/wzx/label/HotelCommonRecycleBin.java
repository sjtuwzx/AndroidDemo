package com.wzx.label;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;

/**
 * Created by wang_zx on 2015/12/23.
 */
public class HotelCommonRecycleBin {

    private ArrayMap<Class, ArrayList> mScrapCache = new ArrayMap<Class, ArrayList>();

    public <T> T getScrapObject(Class<T> clz) {
        ArrayList scrapObjects = mScrapCache.get(clz);
        if (scrapObjects != null && !scrapObjects.isEmpty()) {
            return (T)scrapObjects.remove(0);
        }
        return null;
    }

    public void addScrapObject(Object scrap) {
        Class clz = scrap.getClass();
        if (!mScrapCache.containsKey(clz)) {
            ArrayList scrapObjects = new ArrayList(8);
            mScrapCache.put(clz, scrapObjects);
        }
        ArrayList scrapObjects = mScrapCache.get(clz);
        scrapObjects.add(scrap);
    }

}
