package com.thssh.hotfix.robust.downloader;

import android.text.TextUtils;

import com.meituan.robust.Patch;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.model.CryptPatch;
import com.thssh.hotfix.util.IoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
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
        AppLog.step(5, "do download patch file sync.");
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Response response = null;
        try {
            response = client.newCall(request).execute();
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
            AppLog.step(5, "download success.");
        } catch (IOException e) {
            AppLog.step(5, "download patch exception. " + e.getMessage());
            return false;
        } catch (Exception e) {
            AppLog.step(5, "download patch exception. " + e.getMessage());
        } finally {
            IoUtils.closeSilently(inputStream);
            IoUtils.closeSilently(outputStream);
            if (response != null) response.close();
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
        AppLog.step(1, "fetch patch list.");
        Request request = new Request.Builder()
                .get()
                .url("http://yapi.demo.qunar.com/mock/27462/ceshi/patches?versionCode=0")
                .build();
        Call call = client.newCall(request);
        List<Patch> patches = new ArrayList<>(5);
        Response response = null;
        try {
            response = call.execute();
            String json = response.body().string();
            JSONArray ja = new JSONArray(json);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                CryptPatch patch = new CryptPatch();
                patch.setName(jo.getString("name"));
                patch.setMd5(jo.getString("md5"));
                patch.setAppHash(jo.getString("appHash"));
                patch.setLocalPath(dir + File.separator + "patch");
                patch.setTempMD5(jo.getString("tmpMD5"));
                patch.setUrl(jo.getString("path"));
                patch.setRsaKey(jo.getString("password"));
                patch.setPatchesInfoImplClassFullName(PKG_NAME + DOT + CLZ_NAME);
                patches.add(patch);
            }
            AppLog.step(1, "patch list size: " + patches.size());
            response.close();
        } catch (IOException e) {
            AppLog.step(1, "fetch patch list io exception. " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            AppLog.step(1, "fetch patch list  json exception. " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) response.close();
        }

        return patches;
    }
}
