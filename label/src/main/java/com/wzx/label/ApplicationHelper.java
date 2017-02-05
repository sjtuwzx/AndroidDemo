package com.wzx.label;

import android.app.Application;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * Created by wangzhenxing on 16/11/3.
 */

public final class ApplicationHelper {

    private Application mApplication;

    public static ApplicationHelper getInstance() {
        return ApplicationHelper.SingleInstanceHolder.INSTANCE;
    }

    private static class SingleInstanceHolder {
        private static final ApplicationHelper INSTANCE = new ApplicationHelper();
    }

    private ApplicationHelper() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                final Class<?> activityThreadClass =
                        Class.forName("android.app.ActivityThread");
                final Method method = activityThreadClass.getMethod("currentApplication");
                mApplication = (Application) method.invoke(null, (Object[]) null);
            }
        } catch (final Exception e) {
        }
    }

    public Application getApplication() {
        return mApplication;
    }

}
