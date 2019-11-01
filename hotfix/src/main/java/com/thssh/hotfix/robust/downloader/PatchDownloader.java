package com.thssh.hotfix.robust.downloader;

import com.meituan.robust.Patch;

import java.util.List;

public interface PatchDownloader {
    boolean downloadSync(String url, String path);
    void download(String url, String path, Callback callback);

    List<Patch> fetchPatchListSync();
}
