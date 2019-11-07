package com.thssh.hotfix.robust;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchExecutor;
import com.meituan.robust.RobustCallBack;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.downloader.Callback;
import com.thssh.hotfix.robust.downloader.DefaultPatchDownloader;
import com.thssh.hotfix.robust.downloader.PatchDownloader;
import com.thssh.hotfix.robust.processor.DefaultPatchProcessor;
import com.thssh.hotfix.robust.processor.PatchProcessor;
import com.thssh.hotfix.robust.processor.ProcessException;
import com.thssh.hotfix.robust.repoter.DefaultReport;
import com.thssh.hotfix.robust.repoter.Reporter;
import com.thssh.hotfix.util.IoUtils;
import com.thssh.hotfix.util.MD5Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class HotPatchManager {
    //./gradlew clean  assembleRelease --stacktrace --no-daemon
    // adb push patch.jar /sdcard/Android/data/com.bbae.anno/files/loader
    private static HotPatchManager ins;

    private static final String TAG_PATCH_STATUS = "robust_patch_status";

    private PatchExecutor executor;
    private PatchDownloader downloader;
    private PatchProcessor processor;
    private Reporter reporter;

    private PatchManipulateImpl mPatchManipulate;

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
        this.reporter = new DefaultReport();
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
                AppLog.step(12, "patch result: " + (result?"success.": "failed"));
                if (result) {
                    doReport("success");
                    if (mPatchManipulate != null) {
                        mPatchManipulate.addCache(patch.getMd5());
                    }
                } else {
                    doReport("failed_in_patch_" + patch.getName());
                }
                AppLog.d("onPatchApplied: result: " + result +
                        ", isAppliedSuccess: " + patch.isAppliedSuccess() +
                        ", md5: " + patch.getMd5()
                );
                AppLog.step(0, "Robust Finished!");
            }

            @Override
            public void logNotify(String log, String where) {
                doReport("failed_log: " + log + "|where: " + where);
                AppLog.d("logNotify: log: " + log + " - where: " + where);
            }

            @Override
            public void exceptionNotify(Throwable throwable, String where) {
                AppLog.d("exceptionNotify: where: " + where + " - exception: " + throwable.getMessage());
                doReport("patch_failed_with_exception_" + throwable.getMessage());
            }
        });
    }

    private void doReport(String status) {
        if (null == reporter) return;
        reporter.report(TAG_PATCH_STATUS, status);
    }

    private Context mContext;
    private RobustCallBack mCallback;
    public HotPatchManager init(Context context, RobustCallBack callback) {
        AppLog.d("init: ");
        if (downloader == null) {
            throw new IllegalArgumentException("PatchDownloader cannot be null");
        }
        if (processor == null) {
            throw new IllegalArgumentException("PatchProcessor cannot be null");
        }
        this.mContext = context;
        this.mCallback = callback;
        mPatchManipulate = new PatchManipulateImpl(downloader, processor, reporter);

        return this;
    }

    public void execute() {
        AppLog.step(0, "Robust Start!");
        if (executor != null) {
            executor.interrupt();
        }
        executor = new PatchExecutor(
                mContext,
                mPatchManipulate,
                mCallback
        );
        executor.start();
    }

}
