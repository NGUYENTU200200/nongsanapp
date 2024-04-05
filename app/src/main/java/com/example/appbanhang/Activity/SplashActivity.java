package com.example.appbanhang.Activity;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.appbanhang.R;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        Paper.init(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                }catch (Exception e){

                }finally {
                    if(Paper.book().read("user")==null){
                        Intent i = new Intent(getApplicationContext(),DangNhapActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        Intent home = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(home);
                        finish();
                    }
                }
            }
        });
        thread.start();
    }
}