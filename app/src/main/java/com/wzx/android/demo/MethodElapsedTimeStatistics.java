package com.wzx.android.demo;

import android.os.Looper;
import android.util.Log;

/**
 * Created by wang_zx on 2014/12/17.
 */
public class MethodElapsedTimeStatistics {

    private static final String TAG = MethodElapsedTimeStatistics.class.getSimpleName();

    private long mStartTimeMs = 0;

    private boolean mIsInMainThread = true;

    private String mCallerInfo = null;

    public void start() {
        mIsInMainThread = Thread.currentThread() == Looper.getMainLooper().getThread();

        RuntimeException here = new RuntimeException("here");
        here.fillInStackTrace();
        StackTraceElement[] elements = here.getStackTrace();
        if (elements.length > 1) {
            StackTraceElement element = elements[1];
            mCallerInfo = String.format("%s(%d)::%s", element.getClassName(), element.getLineNumber(), element
                    .getMethodName());
        }
        mStartTimeMs = System.currentTimeMillis();
    }

    public void finish() {
        long delayMs = System.currentTimeMillis() - mStartTimeMs;
        Log.i(TAG, String.format("[%b, %d]%s", mIsInMainThread, delayMs, mCallerInfo));
    }
}
