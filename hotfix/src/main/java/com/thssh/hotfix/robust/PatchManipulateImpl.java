package com.thssh.hotfix.robust;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchManipulate;
import com.meituan.robust.RobustApkHashUtils;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.downloader.PatchDownloader;
import com.thssh.hotfix.robust.model.CryptPatch;
import com.thssh.hotfix.robust.processor.PatchProcessor;
import com.thssh.hotfix.robust.repoter.Reporter;
import com.thssh.hotfix.util.MD5Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class PatchManipulateImpl extends PatchManipulate {

    private static final String PATCH_DIR = "patches";
    private static final String PATCH_NAME = "patch";

    private PatchDownloader downloader;
    private PatchProcessor processor;
    private Reporter reporter;


    public PatchManipulateImpl(PatchDownloader downloader, PatchProcessor processor, Reporter reporter) {
        this.downloader = downloader;
        this.processor = processor;
        this.reporter = reporter;
    }

    public PatchManipulateImpl(PatchDownloader downloader, PatchProcessor processor) {
        this(downloader, processor, null);
    }

    @Override
    protected List<Patch> fetchPatchList(Context context) {
        AppLog.d("fetchPatchList: ");
        return downloader.fetchPatchListSync(getLocalDir(context) + File.separator + PATCH_DIR);
    }

    @Override
    protected boolean verifyPatch(Context context, Patch patch) {
        AppLog.d("verifyPatch: ");
//        if (!assertFileMD5(patch.getLocalPath(), patch.getMd5())) {
//            return false;
//        }
//        MD5Tools.toMD5(patch.getName()
        patch.setTempPath(context.getCacheDir() + File.separator + PATCH_NAME);
        String robustApkHash = RobustApkHashUtils.readRobustApkHash(context);
        AppLog.d("verifyPatch: robustApkHash: " + robustApkHash);
//        if (robustApkHash != null) {
//            isVerified = true;
//        }
        if (patch instanceof CryptPatch) {
            try {
                String rsaContent = ((CryptPatch) patch).getRsaKey();
                AppLog.d("verifyPatch: rsa content: " + rsaContent);
                processor.decodePatch(context, rsaContent, patch);
            } catch (Exception e) {
                doReport("faild_with_decode_" + e.getMessage());
                AppLog.e("verifyPatch: decodePatch exception: ", e);
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean ensurePatchExist(Patch patch) {
        AppLog.d("ensurePatchExist: ");
        if (assertFileMD5(patch.getLocalPath(), patch.getMd5())) return true;

        boolean success = downloader.downloadSync(patch.getUrl(), patch.getLocalPath());
        return success; // && assertFileMD5(patch.getLocalPath(), patch.getMd5());
    }

    private boolean assertFileMD5(String path, String except) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(except)) return false;
        File targetFile = new File(path);
        if (!targetFile.exists()) return false;
        try {
            String md5 = MD5Tools.getFileMD5(targetFile);
            AppLog.d("assertFileMD5: except md5: " + except);
            AppLog.d("assertFileMD5: file md5: " + md5);
            return TextUtils.equals(md5, except);
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private String getLocalDir(Context context) {
        String packageHome = "/Android/data/" + context.getPackageName() + "/files";
        return new File(Environment.getExternalStorageDirectory(), packageHome).getAbsolutePath();
    }


    private static final String TAG_PATCH_STATUS = "robust_patch_status";
    private void doReport(String status) {
        if (reporter == null) return;
        reporter.report(TAG_PATCH_STATUS, status);
    }
}
