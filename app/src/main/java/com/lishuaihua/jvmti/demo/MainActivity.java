package com.lishuaihua.jvmti.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.lishuaihua.jvmti.Monitor;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Monitor.release();
    }

    public void test(View view) {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    JavaBean javaBean = new JavaBean();

                }
            }
        }.start();
    }

    public void gc(View view) {
        System.gc();
        System.runFinalization();
    }
}