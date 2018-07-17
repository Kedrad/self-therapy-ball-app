package com.kedrad.selftherapyball;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ohoussein.playpause.PlayPauseView;

public class ExerciseActivity extends AppCompatActivity {

    private boolean isChecked;
    private TextView tvTimer;
    private boolean isCountdownRunning = false;
    private ImageView animatedView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Mięśnie podpotyliczne");

        tvTimer = (TextView) findViewById(R.id.timer);
        animatedView = (ImageView) findViewById(R.id.iv_movement_direction);

        final PlayPauseView view = (PlayPauseView) findViewById(R.id.play_pause_view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCountdownRunning){
                    startCountown();
                    view.toggle();
                }
                
            }
        });
    }

    private void startCountown() {
        isCountdownRunning = true;
        isChecked = !isChecked;

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText("" + millisUntilFinished / 1000);

                if((millisUntilFinished / 1000) == 30){
                    changeDirection();

                }
            }

            public void onFinish() {

                //Play sound
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.exquisite);
                mp.start();
                if (Build.VERSION.SDK_INT >= 26) {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
                } else {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                }

                Snackbar.make(tvTimer, "Koniec!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                tvTimer.setText("60");

            }
        }.start();
    }

    public void changeDirection(){
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
        ImageView animatedView;
        animatedView = (ImageView) findViewById(R.id.iv_movement_direction);
        //isChecked = !isChecked;
        final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
        animatedView.setImageState(stateSet, true);
        Snackbar.make(tvTimer, "Zmień kierunek ruchu.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        TextView textView = findViewById(R.id.tv_movement_direction);
        textView.setText("Poruszaj głową w górę i w dół");
    }

}
