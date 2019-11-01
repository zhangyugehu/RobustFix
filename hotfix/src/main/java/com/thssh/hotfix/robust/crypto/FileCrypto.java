package com.thssh.hotfix.robust.crypto;

import java.io.File;

public interface FileCrypto {

    void encrypt(File src, File encryptDest, String key);

    void decrypt(File encryptSrc, File decryptDest, String key);

    void encrypt(String srcPath, String encryptDestPath, String key);

    void decrypt(String encryptSrcPath, String decryptDestPath, String key);
}
