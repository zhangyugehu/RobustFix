package com.thssh.robustfix;

import android.app.Application;

import com.thssh.hotfix.robust.HotPatchManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HotPatchManager.getIns().init(getApplicationContext()).execute();
    }
}
