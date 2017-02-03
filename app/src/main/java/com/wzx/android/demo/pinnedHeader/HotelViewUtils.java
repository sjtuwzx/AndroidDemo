package com.wzx.android.demo.pinnedHeader;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

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

    public static void setImageAlpha(ImageView imageView, float alpha) {
        setImageAlpha(imageView, (int) (alpha * 255));
    }

    public static void setImageAlpha(ImageView imageView, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setImageAlpha(alpha);
        } else {
            imageView.setAlpha(alpha);
        }
    }

    public static int blendColor(int scrColor, int dstColor, float factor) {
        int scrAlpha = Color.alpha(scrColor);
        int scrRed = Color.red(scrColor);
        int scrGreen = Color.green(scrColor);
        int scrBlue = Color.blue(scrColor);

        int dstAlpha = Color.alpha(dstColor);
        int dstRed = Color.red(dstColor);
        int dstGreen = Color.green(dstColor);
        int dstBlue = Color.blue(dstColor);

        int outputAlpha = (int)(scrAlpha * (1 - factor) + dstAlpha * factor) & 0xFF;
        int outputRed = (int)(scrRed * (1 - factor) + dstRed * factor) & 0xFF;
        int outputGreen = (int)(scrGreen * (1 - factor) + dstGreen * factor) & 0xFF;
        int outputBlue = (int)(scrBlue * (1 - factor) + dstBlue * factor) & 0xFF;

        return Color.argb(outputAlpha, outputRed, outputGreen, outputBlue);
    }
}
