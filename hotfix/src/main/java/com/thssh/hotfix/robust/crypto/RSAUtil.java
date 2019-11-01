package com.thssh.hotfix.robust.crypto;

import android.util.Base64;

import com.thssh.common.log.AppLog;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.Key;

import javax.crypto.Cipher;

public class RSAUtil {
    /** 指定加密算法为RSA */
//    private static final String ALGORITHM = "RSA";
    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

//    /** 指定公钥存放文件 */
//    private static String PUBLIC_KEY_FILE = "PublicKey";
//    /** 指定私钥存放文件 */
//    private static String PRIVATE_KEY_FILE = "PrivateKey";

//    public static void main(String[] args) throws Exception {
//
//        String source = UUID.randomUUID().toString();// 要加密的字符串
//        System.out.println("准备用私钥加密的字符串为：");
//        System.out.println(source);
//
//        String cryptograph = encrypt(source);// 生成的密文
//        System.out.println("用公钥私钥后的结果为:");
//        System.out.println(cryptograph);
//        System.out.println();
//
//        String target = decrypt(cryptograph);// 解密密文
//        System.out.println("用公钥解密后的字符串为：");
//        System.out.println(target);
//        System.out.println();
//    }

    /**
     * 加密方法
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String source, InputStream keyStream) throws Exception {

        Key privateKey = getKey(keyStream);
        AppLog.d("encrypt: 私钥为：");
        AppLog.d(Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT));

        /* 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] b = source.getBytes();
        /* 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        return Base64.encodeToString(b1, Base64.DEFAULT);
    }

    /**
     * 解密算法
     * @param rsaContent    密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String rsaContent, InputStream keyStream) throws Exception {

        Key publicKey = getKey(keyStream);

        AppLog.d("decrypt: 公钥为：");
        AppLog.d(Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));

        /* 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] rsaContentBytes = Base64.decode(rsaContent, Base64.DEFAULT);

        /* 执行解密操作 */
        byte[] contentBytes = cipher.doFinal(rsaContentBytes);
        return new String(contentBytes);
    }

//    private static Key getKey(String fileName) throws Exception {
//        Key key;
//        ObjectInputStream ois = null;
//        try {
//            /** 将文件中的私钥对象读出 */
//            ois = new ObjectInputStream(new FileInputStream(fileName));
//            key = (Key) ois.readObject();
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            ois.close();
//        }
//        return key;
//    }

    private static Key getKey(InputStream is) throws Exception {
        Key key;
        ObjectInputStream ois = null;
        try {
            /* 将文件中的私钥对象读出 */
            ois = new ObjectInputStream(is);
            key = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }
        return key;
    }
}
