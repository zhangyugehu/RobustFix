package com.thssh.robustfix;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;

import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.HotPatchManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.setDebug(isApbDebuggable(this));
        HotPatchManager.getIns().init(getApplicationContext()).execute();
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
