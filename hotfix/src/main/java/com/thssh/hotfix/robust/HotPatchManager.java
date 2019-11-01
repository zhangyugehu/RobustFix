package com.thssh.hotfix.robust;

import android.content.Context;

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

public class HotPatchManager {
    //./gradlew clean  assembleRelease --stacktrace --no-daemon
    // adb push patch.jar /sdcard/Android/data/com.bbae.anno/files/loader
    private static HotPatchManager ins;

    private static final String TAG_PATCH_STATUS = "robust_patch_status";

    private PatchExecutor executor;
    private PatchDownloader downloader;
    private PatchProcessor processor;
    private Reporter reporter;

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
//        this.downloader = new PatchDownloader() {
//            @Override
//            public boolean downloadSync(String url, String path) {
//                return true;
//            }
//
//            @Override
//            public void download(String url, String path, Callback callback) {
//                AppLog.d("download: url: " + url + ", path: " + path);
//            }
//
//            @Override
//            public List<Patch> fetchPatchListSync(String dir) {
//                AppLog.d("fetchPatchListSync: dir: " + dir);
//                Patch patch = new Patch();
//                patch.setLocalPath(dir + File.separator + "patch");
//                patch.setPatchesInfoImplClassFullName("com.thssh.hotfix.patch.PatchesInfoImpl");
//                return Arrays.asList(
//                        patch
//                );
//            }
//        };
//        this.processor = new PatchProcessor() {
//            @Override
//            public void decodePatch(String cryptoGraph, Patch patch) throws ProcessException {
//                try {
//                    IoUtils.copyStream(new FileInputStream(new File(patch.getLocalPath())),
//                            new FileOutputStream(new File(patch.getTempPath())), new IoUtils.CopyListener() {
//                                @Override
//                                public boolean onBytesCopied(int current, int total) {
//                                    AppLog.d("onBytesCopied: process: " + current + "/" + total);
//                                    return false;
//                                }
//                            });
//                    patch.setMd5(MD5Tools.getFileMD5(new File(patch.getTempPath())));
//                } catch (Exception e) {
//                    throw new ProcessException(e.getMessage());
//                }
//            }
//
//            @Override
//            public void decodePatch(Context context, String cryptoGraph, Patch patch) throws ProcessException {
//
//            }
//        };
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
                if (result) {
                    doReport("success");
                } else {
                    doReport("failed_in_patch_" + patch.getName());
                }
                AppLog.d("onPatchApplied: result: " + result +
                        ", isAppliedSuccess: " + patch.isAppliedSuccess() +
                        ", md5: " + patch.getMd5()
                );
            }

            @Override
            public void logNotify(String log, String where) {
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

    public HotPatchManager init(Context context, RobustCallBack callback) {
        AppLog.d("init: ");
        if (downloader == null) {
            throw new IllegalArgumentException("PatchDownloader cannot null");
        }

        executor = new PatchExecutor(
                context,
                new PatchManipulateImpl(downloader, processor, reporter),
                callback
        );
        return this;
    }

    public void execute() {
        executor.start();
    }
}
