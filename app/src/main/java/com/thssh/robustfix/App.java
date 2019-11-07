package com.thssh.robustfix;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.AppStateHelper;
import com.thssh.hotfix.robust.HotPatchManager;

import java.util.List;

public class App extends Application {

    private AppStateHelper mAppStateHelper;
    private HotPatchManager mPatchManager;

    @Override
    public void onCreate() {
        super.onCreate();
//        AppLog.setDebug(isApbDebuggable(this));
        AppLog.d("onCreate: ");
        mPatchManager = HotPatchManager.getIns().init(getApplicationContext());
        mAppStateHelper = new AppStateHelper();
        mAppStateHelper.register(this, new AppStateHelper.OnAppStateListener() {
            @Override
            public void onStateChanged(boolean isBackground) {
                AppLog.d("onStateChanged: " + isBackground);
                if (!isBackground) {
                    AppLog.d("onStateChanged: App: " + App.this.hashCode());
                    AppLog.d("onStateChanged: mPatchManager: " + mPatchManager.hashCode());
                    mPatchManager.execute();
                }
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppLog.d("onTerminate: ");
        mAppStateHelper.unregister(this);
    }

    /**
     * 判断应用是否是在后台
     */
        public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (TextUtils.equals(appProcess.processName, context.getPackageName())) {
                boolean isBackground = (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                return isBackground || isLockedState;
            }
        }
        return false;
    }

    public static boolean isApbDebuggable(@NonNull Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
