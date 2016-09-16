package com.harryio.orainteractive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;
import com.harryio.orainteractive.ui.auth.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Show splash screen for SPLASH_DISPLAY_DURATION
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Decide which activity is to be launched depending on the logged in status of the user.
                //If user is not logged in then launch LoginActivity so that the user can login/register.
                //Otherwise if the user is logged in direct user to the MainActivity
                boolean isLoggedIn = PrefUtils.getInstance(SplashActivity.this)
                        .get(PrefUtils.KEY_IS_LOGGED_IN, false);
                Class toBeLaunchedActivity = isLoggedIn ? MainActivity.class : LoginActivity.class;
                Intent intent = Utils.getClearStackIntent(SplashActivity.this, toBeLaunchedActivity);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}