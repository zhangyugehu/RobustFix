package com.thssh.hotfix.robust.repoter;

import android.util.Log;

public class DefaultReport implements Reporter {
    @Override
    public void report(String tag, Object obj) {
        Log.d("Reporter", "tag: " + tag + ", obj: " + obj);
    }
}
