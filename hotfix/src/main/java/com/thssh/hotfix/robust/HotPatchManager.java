package com.thssh.hotfix.robust;

import android.content.Context;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchExecutor;
import com.meituan.robust.RobustCallBack;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.downloader.DefaultPatchDownloader;
import com.thssh.hotfix.robust.downloader.PatchDownloader;
import com.thssh.hotfix.robust.processor.DefaultPatchProcessor;
import com.thssh.hotfix.robust.processor.PatchProcessor;

import java.util.List;

public class HotPatchManager {
    //./gradlew clean  assembleRelease --stacktrace --no-daemon
    // adb push patch.jar /sdcard/Android/data/com.bbae.anno/files/loader
    private static HotPatchManager ins;

    private PatchExecutor executor;
    private PatchDownloader downloader;
    private PatchProcessor processor;

    public static HotPatchManager getIns() {
        if (ins == null) {
            synchronized (HotPatchManager.class) {
                if (ins == null) {
                    ins = new HotPatchManager();
                }
            }
        }
        return ins;
    }

    private HotPatchManager() {
        this.downloader = new DefaultPatchDownloader();
        this.processor = new DefaultPatchProcessor();
    }

    public HotPatchManager setDownloader(PatchDownloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public HotPatchManager setProcessor(PatchProcessor processor) {
        this.processor = processor;
        return this;
    }

    public HotPatchManager init(Context context) {
        return init(context, new RobustCallBack() {
            @Override
            public void onPatchListFetched(boolean result, boolean isNet, List<Patch> patches) {
                AppLog.d("onPatchListFetched: result: " + result);
            }

            @Override
            public void onPatchFetched(boolean result, boolean isNet, Patch patch) {
                AppLog.d("onPatchFetched: result: " + result);
            }

            @Override
            public void onPatchApplied(boolean result, Patch patch) {
                AppLog.d("onPatchApplied: result: " + result);
            }

            @Override
            public void logNotify(String log, String where) {
                AppLog.d("logNotify: log: " + log + " - where: " + where);
            }

            @Override
            public void exceptionNotify(Throwable throwable, String where) {
                AppLog.d("exceptionNotify: where: " + where + " - exception: " + throwable.getMessage());
            }
        });
    }
    public HotPatchManager init(Context context, RobustCallBack callback) {
        AppLog.d("init: ");
        if (downloader == null) {
            throw new IllegalArgumentException("PatchDownloader cannot null");
        }

        executor = new PatchExecutor(context, new PatchManipulateImpl(downloader, processor), callback);
        return this;
    }

    public void execute() {
        executor.start();
    }
}
