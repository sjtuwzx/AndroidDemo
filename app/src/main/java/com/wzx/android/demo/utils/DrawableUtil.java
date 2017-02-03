package com.wzx.android.demo.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by liupei on 2016/11/2.
 */

public final class DrawableUtil {

    private DrawableUtil() {
    }

    public static Drawable getTintDrawable(Drawable resource, int color) {
        if (resource != null) {
            Drawable wrappedDrawable = DrawableCompat.wrap(resource);
            DrawableCompat.setTint(wrappedDrawable, color);
            return wrappedDrawable;
        } else {
            return null;
        }
    }

}
