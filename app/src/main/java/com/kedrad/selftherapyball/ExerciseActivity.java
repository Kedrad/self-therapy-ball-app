package com.kedrad.selftherapyball;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.shadow.ShadowGenerator;
import carbon.shadow.Shadow;
import carbon.shadow.ShadowShape;
import carbon.shadow.ShadowView;
import carbon.widget.RenderingMode;
import carbon.Carbon;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.ohoussein.playpause.PlayPauseView;

import java.util.Timer;
import java.util.TimerTask;

public class ExerciseActivity extends AppCompatActivity {

    //ID of selected exercise plan from the menu activity
    private int selectedPlanId;
    //ID of selected muscle from the list in the exercise plan
    private int selectedMuscleId;
    //Name of the selected plan used for obtaining arrays from the resources
    private String selectedPlanName;

    private String[] instructionImages;
    private String[] instructionImagesAfter30Seconds;

    private String[] movementDirectionTexts = new String[2];

    private boolean isChecked = true;
    private boolean isCountdownRunning = false;

    private float progress = 0;

    private CountDownTimer textViewTimer;
    private CountDownTimer progressBarTimer;

    //Variable storing remaining milliseconds used for pausing and resuming timer
    private long millisRemaining = 60000;

    private SlideShow slideShow;

    @BindView(R.id.timer) TextView tvTimer;
    @BindView(R.id.image_switcher_instruction) ImageSwitcher imageSwitcher;
    @BindView(R.id.progress_bar) CircularProgressBar progressBar;
    @BindView(R.id.play_pause_view) PlayPauseView playPauseView;
    @BindView(R.id.tv_ball_location) TextView tvBallLocation;
    @BindView(R.id.tv_movement_direction) TextView tvMovementDirection;
    @BindView(R.id.iv_movement_direction) ImageView ivMovementDirection;
    @BindView(R.id.fab_previous_exercise) FloatingActionButton fabPreviousExercise;
    @BindView(R.id.fab_next_exercise) FloatingActionButton fabNextExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Receiving selected plan ID from the menu activity
        selectedPlanId = getIntent().getIntExtra("selectedPlan", 0);

        //Receiving selected muuscle ID from the previous activity
        selectedMuscleId = getIntent().getIntExtra("selectedMuscle", 0);
        selectedPlanName = getResources().getStringArray(R.array.main_menu_items)[selectedPlanId];

        //Obtaining images array from the xml resources
        int instructionImagesArrayId = this.getResources().
                getIdentifier(selectedPlanName + "_images", "array", this.getPackageName());
        int instructionImagesAfter30SecondsArrayId = this.getResources().
                getIdentifier(selectedPlanName + "_images_after_30_seconds", "array", this.getPackageName());

        instructionImages = getResources().getStringArray(instructionImagesArrayId)[selectedMuscleId].trim().split("\\s+");
        instructionImagesAfter30Seconds = getResources().getStringArray(instructionImagesAfter30SecondsArrayId)[selectedMuscleId].trim().split("\\s+");

        //Instantiating a slideshow on top of the activity that shows how to perform the exercise
        slideShow = new SlideShow(imageSwitcher, instructionImages, instructionImagesAfter30Seconds, this);


        //Setting the title to selected muscle name
        setTitle(getResources().getStringArray(this.getResources().
                getIdentifier(selectedPlanName , "array", this.getPackageName()))[selectedMuscleId]);

        prepareViews();

        setOnClickListeners();

    }

    private void setOnClickListeners(){
        playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseView.toggle();

                if(!isCountdownRunning){
                    startCountdown(millisRemaining);
                }
                else {
                    pauseCountdown();
                    isCountdownRunning = !isCountdownRunning;
                }
            }
        });


        fabPreviousExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, ExerciseActivity.class);

                intent.putExtra("selectedMuscle", selectedMuscleId - 1);
                intent.putExtra("selectedPlan", selectedPlanId);
                startActivity(intent);
            }
        });

        fabNextExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, ExerciseActivity.class);

                intent.putExtra("selectedMuscle", selectedMuscleId + 1);
                intent.putExtra("selectedPlan", selectedPlanId);
                startActivity(intent);
            }
        });
    }

    private void prepareViews(){
        //Obtaining string arrays from the xml resources
        int ballLocationArrayId = this.getResources().
                getIdentifier(selectedPlanName + "_ball_location", "array", this.getPackageName());
        int movementDirectionArrayId = this.getResources().
                getIdentifier(selectedPlanName + "_movement_direction", "array", this.getPackageName());
        int movementDirectionAfter30SecondsArrayId = this.getResources().
                getIdentifier(selectedPlanName + "_movement_direction_after_30_seconds", "array", this.getPackageName());

        tvBallLocation.setText(getResources().getStringArray(ballLocationArrayId)[selectedMuscleId]);

        //Initial movement direction
        movementDirectionTexts[0] = getResources().getStringArray(movementDirectionArrayId)[selectedMuscleId];
        //Movement direction after 30 seconds
        movementDirectionTexts[1] = getResources().getStringArray(movementDirectionAfter30SecondsArrayId)[selectedMuscleId];

        tvMovementDirection.setText(movementDirectionTexts[0]);

        //On the first exercise disable the previous button
        if(selectedMuscleId == 0) {
            fabPreviousExercise.setEnabled(false);
            fabPreviousExercise.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.disabled_button_background)));
        }
        //On the last exercise disable the next button
        if(selectedMuscleId == (getResources().getStringArray(getResources().getIdentifier(selectedPlanName,  "array", this.getPackageName()))).length - 1) {
            fabNextExercise.setEnabled(false);
            fabNextExercise.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.disabled_button_background)));
        }
    }


    private void startCountdown(long duration) {
        //When countdown is being started for the first time, start the slideshow
        if(millisRemaining == 60000)
            slideShow.start();
        //If it is restarted after full exercise, restart the exercise
        if(!isChecked)
            restartExercise();

        isCountdownRunning = true;

        Activity activity = this;

        textViewTimer = new CountDownTimer(duration, 1000) {

            public void onTick(long millisUntilFinished) {
                //Stop the countdown when user left the activity
                if(activity.isFinishing())
                    this.cancel();

                millisRemaining = millisUntilFinished;

                tvTimer.setText(Long.toString(millisUntilFinished / 1000));

                if((millisUntilFinished / 1000) == 30){
                    changeDirection();
                }
            }


            public void onFinish() {
                vibrate();
                playSound();

                //Show a message on the bottom of the screen
                Snackbar.make(tvTimer, R.string.exercise_end_message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                playPauseView.toggle();

                //Reset the timer
                millisRemaining = 60000;
                tvTimer.setText(Long.toString(millisRemaining / 1000));

                slideShow.stop();
            }
        }.start();
        
        progressBarTimer = new CountDownTimer(duration, 100) {

            public void onTick(long millisUntilFinished) {
                progress += 100;
                progressBar.setProgress(progress);
            }


            public void onFinish() {
                progress = 0;
                progressBar.setProgress(0f);
                isCountdownRunning = !isCountdownRunning;
            }
        }.start();

    }

    public void pauseCountdown(){
        textViewTimer.cancel();
        progressBarTimer.cancel();
    }

    public void changeDirection(){
        vibrate();
        playSound();

        animateMovementDirectionIcon();

        //Show a message on the bottom of the screen
        Snackbar.make(tvTimer, R.string.change_movement_direction_message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        //Switch to the second image set
        slideShow.changeToSecondImageArray();
        //Change movement direction text
        tvMovementDirection.setText(movementDirectionTexts[1]);
    }

    public void vibrate(){
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(550,10));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(550);
        }
    }

    public void playSound(){
        MediaPlayer mp;

        if(millisRemaining == 0)
            mp = MediaPlayer.create(getApplicationContext(), R.raw.exquisite);
        else
            mp = MediaPlayer.create(getApplicationContext(), R.raw.exquisite);

        mp.start();
    }

    public void restartExercise() {
        //Switch to the first image set
        slideShow.changeToSecondImageArray();
        //Change movement direction text
        tvMovementDirection.setText(movementDirectionTexts[0]);

        animateMovementDirectionIcon();
    }

    public void animateMovementDirectionIcon(){
        final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
        ivMovementDirection.setImageState(stateSet, true);
        isChecked = !isChecked;
    }

}
