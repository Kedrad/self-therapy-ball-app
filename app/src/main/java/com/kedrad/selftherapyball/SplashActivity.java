package com.kedrad.selftherapyball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kogitune.activity_transition.ActivityTransitionLauncher;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;

        // Check if we need to display intro
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(
                "completed_intro", false)) {
            // The user hasn't seen the intro yet, so show it
            intent = new Intent(this, IntroActivity.class);
        }
        else
            //Start the Main Activity
            intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}