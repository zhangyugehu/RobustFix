package com.thssh.hotfix.robust;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchManipulate;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.downloader.PatchDownloader;
import com.thssh.hotfix.robust.processor.PatchProcessor;
import com.thssh.hotfix.util.MD5Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class PatchManipulateImpl extends PatchManipulate {

    // 和robust.xml中patchPackname必须保持一致
    private static final String PKG_NAME = "com.thssh.hotfix.patch";
    private static final String DOT = ".";
    private static final String CLZ_NAME = "PatchesInfoImpl";
    private static final String PATCH_NAME = "patch";

    private PatchDownloader downloader;
    private PatchProcessor processor;

    // 原始密钥
    private static final String TEST_CRTPTO = "8bb36da6-41e1-4183-8091-890528ad6d19";
    // 私钥加密后的密钥
    private static final String TEST_CRYPTO_GRAPH =
            "FSlwtGRbf7m63fZJaf7Q9UgnvcgmOX2PGN56vvUbT7yGp+rTXjzqnzS23k2Ci+P0SIScknfdDSs4JENH1vknZQ9lh9jdOT/pG6Mt4ViB70ef4WdJx9eHolPWcc1j8Z0C4LDW296BDIsHyAEsZ9qFlyGQ7qD9XZSeDndjNmhX0sI=";

//    private static final String TEST_URL = "https://raw-file.oss-cn-beijing.aliyuncs.com/patch.jar";
    private static final String TEST_URL = "https://raw-file.oss-cn-beijing.aliyuncs.com/123.en";
    private static final String TEST_MD5 = "f99cc91a826537a5f7b276c98ab15311";

    public PatchManipulateImpl(PatchDownloader downloader, PatchProcessor processor) {
        this.downloader = downloader;
        this.processor = processor;
    }

    @Override
    protected List<Patch> fetchPatchList(Context context) {
        // TODO 联网拉取补丁列表
        AppLog.d("fetchPatchList: ");
        File patchDir = new File(getLocalDir(context), "patches");

        downloader.fetchPatchListSync();
        List<Patch> patches = new ArrayList<>(5);
        Patch patch = new Patch();
        // name暂存加密后的密钥
        patch.setName(TEST_CRYPTO_GRAPH);
        patch.setPatchesInfoImplClassFullName(PKG_NAME + DOT + CLZ_NAME);
        patch.setLocalPath(patchDir.getPath() + File.separator + PATCH_NAME);
        patch.setUrl(TEST_URL);
        patch.setMd5(TEST_MD5);
        patches.add(patch);
        return patches;
    }

    @Override
    protected boolean verifyPatch(Context context, Patch patch) {
        AppLog.d("verifyPatch: ");
        // TODO 验证LocalFile hash值
        if (!assertFileMD5(patch.getLocalPath(), patch.getMd5())) {
            return false;
        }
        patch.setTempPath(context.getCacheDir() + File.separator + PATCH_NAME);
//        String robustApkHash = RobustApkHashUtils.readRobustApkHash(context);
//        if (robustApkHash != null) {
//            isVerified = true;
//        }
        try {
            String rsaContent = patch.getName();
            AppLog.d("verifyPatch: rsa content: " + rsaContent);
            processor.decodePatch(context, rsaContent, patch);
        } catch (Exception e) {
            AppLog.e("verifyPatch: decodePatch exception: ", e);
            return false;
        }
        return true;
    }

    @Override
    protected boolean ensurePatchExist(Patch patch) {
        AppLog.d("ensurePatchExist: ");
        boolean success = downloader.downloadSync(patch.getUrl(), patch.getLocalPath());

        return success && new File(patch.getLocalPath()).exists();
    }

    private String getLocalDir(Context context) {
        String packageHome = "/Android/data/" + context.getPackageName() + "/files";
        return new File(Environment.getExternalStorageDirectory(), packageHome).getAbsolutePath();
    }

    private boolean assertFileMD5(String path, String except) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(except)) return false;
        File targetFile = new File(path);
        try {
            String md5 = MD5Tools.getFileMD5(targetFile);
            AppLog.d("assertFileMD5: except md5: " + except);
            AppLog.d("assertFileMD5: file md5: " + md5);
            return TextUtils.equals(md5, except);
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
