package com.thssh.robustfix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
//    @Modify
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        showToast("Modify onCreate. " + getAddMethod());

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

}
