package com.thssh.hotfix.robust.processor;

import android.content.Context;

import com.meituan.robust.Patch;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.crypto.AESFileCrypto;
import com.thssh.hotfix.robust.crypto.FileCrypto;
import com.thssh.hotfix.robust.crypto.RSAUtil;

import java.io.InputStream;

public class DefaultPatchProcessor implements PatchProcessor {

    private FileCrypto fileCrypto;

    public DefaultPatchProcessor() {
        this.fileCrypto = new AESFileCrypto();
    }

    @Override
    public void decodePatch(String cryptoGraph, Patch patch) throws ProcessException {
        throw new ProcessException("not implement yet.");
    }

    @Override
    public void decodePatch(Context context, String cryptoGraph, Patch patch) throws ProcessException {
        try {
            InputStream inputStream = context.getAssets().open("rsa.pub");
            String graph = RSAUtil.decrypt(cryptoGraph, inputStream);
            AppLog.d("decodePatch: decrypt graph: " + graph);
            fileCrypto.decrypt(patch.getLocalPath(), patch.getTempPath(), graph);
        } catch (Exception e) {
            throw new ProcessException(e.getMessage());
        }
    }
}
