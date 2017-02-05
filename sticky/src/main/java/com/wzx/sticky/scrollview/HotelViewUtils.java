package com.wzx.sticky.scrollview;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by wangzhenxing on 16/8/4.
 */
public final class HotelViewUtils {

    private HotelViewUtils() {

    }

    public static int getDescendantRelativeTop(View ancestor, View descendant) {
        if (descendant == null) {
            return -1;
        }

        int top = 0;
        ViewParent parent = descendant.getParent();
        while (parent instanceof ViewGroup) {
            top += descendant.getTop();
            if (parent == ancestor) {
                return top;
            } else {
                descendant = (View)parent;
                parent = descendant.getParent();
            }
        }

        return -1;
    }
}
