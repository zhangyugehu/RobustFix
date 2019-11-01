package com.thssh.common.log;

import android.util.Log;

public class AppLog {
    private static final String TAG = "RobustFixGlobalTag";

    public static void d(String message) {
        Log.d(TAG, message);
    }
    public static void e(String message, Throwable tr) {
        Log.e(TAG, message, tr);
    }
    public static void i(String message) {
        Log.i(TAG, message);
    }
}
