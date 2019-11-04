package com.thssh.hotfix.robust.downloader;

import android.text.TextUtils;

import com.meituan.robust.Patch;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.Config;
import com.thssh.hotfix.robust.model.CryptPatch;
import com.thssh.hotfix.util.IoUtils;
import com.thssh.hotfix.util.MD5Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultPatchDownloader implements PatchDownloader {

    private OkHttpClient client;

    public DefaultPatchDownloader() {
        this.client = new OkHttpClient.Builder().build();
    }

    @Override
    public boolean downloadSync(String url, String path) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) return false;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Response response = client.newCall(request).execute();
            inputStream = response.body().byteStream();
            if (inputStream == null) return false;
            File destFile = newAndCreateFile(path);
            outputStream = new FileOutputStream(destFile);
            IoUtils.copyStream(inputStream, outputStream, new IoUtils.CopyListener() {
                @Override
                public boolean onBytesCopied(int current, int total) {
                    AppLog.d("onBytesCopied: download: " + current + "/" + total);
                    return true;
                }
            });
        } catch (IOException e) {
            return false;
        } finally {
            IoUtils.closeSilently(inputStream);
            IoUtils.closeSilently(outputStream);
        }
        return true;
    }

    private File newAndCreateFile(String path) throws IOException {
        File file = new File(path);
        File dir = file.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    // 和robust.xml中patchPackname必须保持一致
    private static final String PKG_NAME = "com.thssh.hotfix.patch";
    private static final String DOT = ".";
    private static final String CLZ_NAME = "PatchesInfoImpl";

    @Override
    public List<Patch> fetchPatchListSync(String dir) {

//        File patchDir = new File(getLocalDir(context), "patches");
//        File patchDir = new File(dir);
        List<Patch> patches = new ArrayList<>(5);
        CryptPatch patch = new CryptPatch();
        // name暂存加密后的密钥
        patch.setName(MD5Tools.toMD5(Config.TEST_CRYPTO_GRAPH));
        patch.setRsaKey(Config.TEST_CRYPTO_GRAPH);
        patch.setPatchesInfoImplClassFullName(PKG_NAME + DOT + CLZ_NAME);
//        String patchName = MD5Tools.toMD5(TEST_CRYPTO_GRAPH);
        patch.setLocalPath(dir + File.separator + "patch");
        patch.setUrl(Config.TEST_URL);
        patch.setMd5(Config.TEST_MD5);
        patches.add(patch);
        return patches;
    }
}
