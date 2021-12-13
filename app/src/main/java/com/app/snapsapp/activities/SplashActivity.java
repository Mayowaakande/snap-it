package com.app.snapsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.app.snapsapp.R;

//Startup screen that shown at very first for few seconds
public class SplashActivity extends AppCompatActivity {
    public static final int SPLASH_TIME_OUT = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        //Run Background thread for 2 seconds to hold on first screen for 2 seconds.
        Thread obj = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        //After 2 seconds start loin screen using Intent
                        Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                        startActivity(intent);
                        finish();

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        };obj.start();

    }
}