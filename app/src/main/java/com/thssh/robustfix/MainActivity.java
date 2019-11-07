package com.thssh.robustfix;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.meituan.robust.patch.annotaion.Add;
import com.meituan.robust.patch.annotaion.Modify;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    @Modify
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
//        showToast("Modify onCreate. " + getAddMethod());

    }

    @Modify
    public void btnClick(View view) {
        tv.setText("Robust Example...");
        tv.setText("Modify. " + getAddMethod());
    }
//
//    @Add
//    private void showToast(String text) {
//        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
//    }
//
    @Add
    private String getAddMethod() {
        return "String From Add Method";
    }

}
