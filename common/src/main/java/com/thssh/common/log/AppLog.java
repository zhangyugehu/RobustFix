package com.thssh.common.log;

import android.util.Log;

public class AppLog {
    private static final String TAG = "RobustFixGlobalTag";

    private static boolean mDebug = false;

    public static void setDebug(boolean debug) {
        AppLog.mDebug = debug;
    }

    public static void d(String message) {
        if (!mDebug) return;
        Log.d(TAG, message);
    }
    public static void e(String message, Throwable tr) {
        Log.e(TAG, message, tr);
    }
    public static void i(String message) {
        Log.i(TAG, message);
    }
}
