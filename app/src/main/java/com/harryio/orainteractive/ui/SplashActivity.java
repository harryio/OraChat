package com.harryio.orainteractive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.ui.auth.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = PrefUtils.getInstance(SplashActivity.this)
                        .get(PrefUtils.KEY_IS_LOGGED_IN);
                Intent intent = isLoggedIn ? new Intent(SplashActivity.this, MainActivity.class)
                        : new Intent(SplashActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}