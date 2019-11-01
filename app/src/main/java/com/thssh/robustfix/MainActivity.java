package com.thssh.robustfix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.meituan.robust.patch.annotaion.Add;
import com.meituan.robust.patch.annotaion.Modify;
import com.thssh.common.log.AppLog;
import com.thssh.hotfix.robust.crypto.FileAESUtil;
import com.thssh.hotfix.util.MD5Tools;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    @Override
//    @Modify
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            encode();
//            decode();
//        } catch (FileNotFoundException e) {
//            AppLog.e("onCreate: ", e);
//        }


//        try {
//            String key = "8bb36da6-41e1-4183-8091-890528ad6d19";
//            AESFileCrypto crypto = new AESFileCrypto();
//            AppLog.d("decode: " + MD5Tools.getFileMD5(new File("/sdcard/123.jpg")));
//
//            crypto.encrypt("/sdcard/123.jpg", "/sdcard/123.e.jpg", key);
//
//            AppLog.d("decode: " + MD5Tools.getFileMD5(new File("/sdcard/123.e.jpg")));
//
//            crypto.decrypt("/sdcard/123.e.jpg", "/sdcard/123.d.jpg", key);
//
//            AppLog.d("decode: " + MD5Tools.getFileMD5(new File("/sdcard/123.d.jpg")));
//        } catch (Exception e) {
//            AppLog.e("onCreate: ", e);
//        }
//        getWindow().getDecorView().getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showToast("Modify onCreate. " + getAddMethod());
//            }
//        }, 1000);
    }
//
//    @Add
//    private void showToast(String text) {
//        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
//    }
//
//    @Add
//    private String getAddMethod() {
//        return null;
//    }

    private void encode() throws FileNotFoundException {
        FileAESUtil crypto = new FileAESUtil();
        String key = "8bb36da6-41e1-4183-8091-890528ad6d19";
        File target = new File("/sdcard/123.jpg");
        AppLog.d("decode: " + MD5Tools.getFileMD5(target));

        File dest = new File("/sdcard/123.e.jpg");
        crypto.encrypt(target, dest, key);
        AppLog.d("decode: " + MD5Tools.getFileMD5(dest));
    }

    private void decode() throws FileNotFoundException {
        FileAESUtil crypto = new FileAESUtil();
        String key = "8bb36da6-41e1-4183-8091-890528ad6d19";
        File target = new File("/sdcard/123.e.jpg");
        AppLog.d("decode: " + MD5Tools.getFileMD5(target));

        File dest = new File("/sdcard/123.d.jpg");
        crypto.decrypt(target, dest, key);
        AppLog.d("decode: " + MD5Tools.getFileMD5(dest));
    }
}
