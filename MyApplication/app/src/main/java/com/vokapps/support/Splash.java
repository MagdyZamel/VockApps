package com.vokapps.support;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vokapps.support.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Log.d("dad","rrrrr");

                    SharedPreferences sp = getSharedPreferences("intercomPrefs", Activity.MODE_PRIVATE);
                    Boolean loggedIN = sp.getBoolean("loggedIn", false);
                    Log.d("dad",loggedIN.toString());

                    if (loggedIN) {
                        Intent intent = new Intent(Splash.this, Home.class);
                        startActivity(intent);
                        finish();


                    }else {

                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                    }
                }
            }
        });
        timer.start();





    }
}
