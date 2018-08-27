package com.kedrad.selftherapyball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.kogitune.activity_transition.ActivityTransition;

import static com.kedrad.selftherapyball.MainActivity.COMPLETED_FIRST_LAUNCH_SHOWCASE_PREF_NAME;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //First slide
        addSlide(AppIntro2Fragment.newInstance(
                getResources().getString(R.string.intro_title_first),
                getResources().getString(R.string.intro_description_first),
                R.drawable.logo_intro_white,
                getResources().getColor(R.color.colorPrimary)
        ));

        //Second slide
        addSlide(new IntroSecondSlide());

        //Third slide
        addSlide(new IntroThirdSlide());

        //Fourth slide
        addSlide(AppIntro2Fragment.newInstance(
                getResources().getString(R.string.intro_title_fourth),
                getResources().getString(R.string.intro_description_fourth),
                R.drawable.help_intro,
                getResources().getColor(R.color.colorPrimary)
        ));

        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.colorPrimary));

        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        //Set the boolean in the preferences so the user won't see the intro on the next lunch
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(IntroActivity.this).edit();
        sharedPreferencesEditor.putBoolean(
                "completed_intro", true);
        sharedPreferencesEditor.apply();

        //Start the Main Activity and tell it to show the showcase
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.STARTED_FROM_SHOWCASE, true);
        //Starting new activity
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
