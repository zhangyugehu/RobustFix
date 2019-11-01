package com.thssh.hotfix.robust.downloader;

import android.text.TextUtils;

import com.meituan.robust.Patch;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.util.IoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @Override
    public void download(String url, String path, Callback callback) {

    }

    @Override
    public List<Patch> fetchPatchListSync() {

        return null;
    }
}
