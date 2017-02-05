package com.wzx.label;

import android.content.Context;

/**
 * Created by wangzhenxing on 17/2/5.
 */

public final class ViewUtils {

    private ViewUtils() {

    }

    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return (int) dipValue;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
