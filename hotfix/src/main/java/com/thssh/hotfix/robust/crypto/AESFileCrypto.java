package com.thssh.hotfix.robust.crypto;

import android.text.TextUtils;

import com.thssh.common.log.AppLog;
import com.thssh.hotfix.util.IoUtils;
import com.thssh.hotfix.util.MD5Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESFileCrypto implements FileCrypto {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;

    public AESFileCrypto() {}

    @Override
    public void encrypt(File src, File encryptDest, String key) {
        key = MD5Tools.toMD5(key);

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        CipherOutputStream cipherOutputStream = null;

        try {
            if (encryptDest.exists()) {
                encryptDest.delete();
            }

            Cipher cipher = getAesCipher(key, Cipher.ENCRYPT_MODE);

            inputStream = new FileInputStream(src);
            outputStream = new FileOutputStream(encryptDest);
            cipherOutputStream = new CipherOutputStream(outputStream, cipher);

            IoUtils.copyStream(inputStream, cipherOutputStream, new IoUtils.CopyListener() {
                @Override
                public boolean onBytesCopied(int current, int total) {
                    AppLog.d("onBytesCopied: encrypted: " + current + "/" + total);
                    return true;
                }
            });

        } catch (Exception e) {
            AppLog.d("decrypt: exception: " + e.getMessage());
        } finally {
            IoUtils.closeSilently(cipherOutputStream);
            IoUtils.closeSilently(inputStream);
            IoUtils.closeSilently(outputStream);
        }
    }

    @Override
    public void decrypt(File encryptSrc, File decryptDest, String key) {
        key = MD5Tools.toMD5(key);

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        CipherOutputStream cipherOutputStream = null;
        try {
            if (decryptDest.exists()) {
                decryptDest.delete();
            }

            Cipher cipher = getAesCipher(key, Cipher.DECRYPT_MODE);

            inputStream = new FileInputStream(encryptSrc);
            outputStream = new FileOutputStream(decryptDest);
            cipherOutputStream = new CipherOutputStream(outputStream, cipher);

            IoUtils.copyStream(inputStream, cipherOutputStream, new IoUtils.CopyListener() {
                @Override
                public boolean onBytesCopied(int current, int total) {
                    AppLog.d("onBytesCopied: decrypted: " + current + "/" + total);
                    return true;
                }
            });

        } catch (Exception e) {
            AppLog.d("decrypt: exception: " + e.getMessage());
        } finally {
            IoUtils.closeSilently(cipherOutputStream);
            IoUtils.closeSilently(inputStream);
            IoUtils.closeSilently(outputStream);
        }

    }

    @Override
    public void encrypt(String srcPath, String encryptDestPath, String key) {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(encryptDestPath)) return;
        encrypt(new File(srcPath), new File(encryptDestPath), key);
    }

    @Override
    public void decrypt(String encryptSrcPath, String decryptDestPath, String key) {
        if (TextUtils.isEmpty(encryptSrcPath) || TextUtils.isEmpty(decryptDestPath)) return;
        decrypt(new File(encryptSrcPath), new File(decryptDestPath), key);
    }

//    private Cipher getAesCipher(String key, int mode)
//            throws Exception {
//
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//        secureRandom.setSeed(key.getBytes());
//        keyGenerator.init(KEY_SIZE, secureRandom);
//        // https://stackoverflow.com/questions/8049872/given-final-block-not-properly-padded
////        keyGenerator.init(KEY_SIZE, new SecureRandom(key.getBytes()));
//        SecretKey secretKey = keyGenerator.generateKey();
//        byte[] codeFormat = secretKey.getEncoded();
//        SecretKeySpec spec = new SecretKeySpec(codeFormat, ALGORITHM);
////        Key k = toKey(key.getBytes());
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(mode, spec);
//        return cipher;
//    }

    public static final String VIPARA = "0102030405060708";

    private Cipher getAesCipher(String key, int mode)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
        SecretKeySpec sKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, sKey, zeroIv);
        return cipher;
    }

    public Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }

//    public void copy(InputStream is, OutputStream os) throws IOException {
//        byte[] cache = new byte[1024];
//        int len = 0;
//        while ((len = is.read(cache)) != -1) {
//            os.write(cache, 0, len);
//            os.flush();
//        }
//    }
}
