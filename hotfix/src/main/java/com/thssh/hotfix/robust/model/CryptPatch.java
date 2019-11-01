package com.thssh.hotfix.robust.model;

import com.meituan.robust.Patch;

public class CryptPatch extends Patch {
    /* rsa加密后的密钥 */
    private String rsaKey;

    public String getRsaKey() {
        return rsaKey;
    }

    public void setRsaKey(String rsaKey) {
        this.rsaKey = rsaKey;
    }
}
