package com.kedrad.selftherapyball;

import android.animation.ObjectAnimator;
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

    private boolean isChecked;
    private TextView tvTimer;
    private boolean isCountdownRunning = false;
    private CircularProgressBar progressBar;


    private ImageSwitcher imageSwitcher;

    private float progress = 0;

    private PlayPauseView playPauseView;

    private CountDownTimer textViewTimer;
    private CountDownTimer progressBarTimer;

    private SlideShow slideShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tvTimer = (TextView) findViewById(R.id.timer);
        playPauseView = (PlayPauseView) findViewById(R.id.play_pause_view);

        //Receiving selected plan ID from the menu activity
        selectedPlanId = getIntent().getIntExtra("selectedPlan", 0);

        //Receiving selected muuscle ID from the precious activity
        selectedMuscleId = getIntent().getIntExtra("selectedMuscle", 0);
        progressBar = findViewById(R.id.progress_bar);

        selectedPlanName = getResources().getStringArray(R.array.main_menu_items)[selectedPlanId];

        //Obtaining images array from the xml resources
        int instructionImagesArrayId = this.getResources().
                getIdentifier(selectedPlanName + "_images", "array", this.getPackageName());
        int instructionImagesAfter30SecondsArrayId = this.getResources().
                getIdentifier(selectedPlanName + "_images_after_30_seconds", "array", this.getPackageName());

        instructionImages = getResources().getStringArray(instructionImagesArrayId)[selectedMuscleId].trim().split("\\s+");
        instructionImagesAfter30Seconds = getResources().getStringArray(instructionImagesAfter30SecondsArrayId)[selectedMuscleId].trim().split("\\s+");

        //Instantiating and starting a slideshow on top of the activity that shows how to perform the exercise
        imageSwitcher = findViewById(R.id.image_switcher_instruction);
        slideShow = new SlideShow(imageSwitcher, instructionImages, instructionImagesAfter30Seconds, this);


        //Setting the title to selected muscle name
        setTitle(getResources().getStringArray(this.getResources().
                getIdentifier(selectedPlanName , "array", this.getPackageName()))[selectedMuscleId]);







        playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCountdownRunning){
                    startCountdown();
                    playPauseView.toggle();
                }
                
            }
        });
    }



    private void startCountdown() {
        slideShow.start();
        isCountdownRunning = true;
        isChecked = !isChecked;
        textViewTimer = new CountDownTimer(60000, 1000) {

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
                playPauseView.toggle();

            }
        }.start();


        progressBarTimer = new CountDownTimer(60000, 100) {

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

   /* public void stopCountdown(){
        textViewTimer.
    }*/

    public void changeDirection(){



        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(550,10));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(550);
        }

        ViewPropertyTransition.Animator animationObject = new ViewPropertyTransition.Animator() {
            @Override
            public void animate(View view) {
                view.setAlpha(0f);

                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
                fadeAnim.setDuration(2500);
                fadeAnim.start();
            }
        };


        ImageView animatedView;
        animatedView = (ImageView) findViewById(R.id.iv_movement_direction);
        //isChecked = !isChecked;
        final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
        animatedView.setImageState(stateSet, true);
        Snackbar.make(tvTimer, "Zmień kierunek ruchu.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        TextView textView = findViewById(R.id.tv_movement_direction);
        textView.setText("Poruszaj głową w górę i w dół");
        slideShow.changeToSecondImageArray();
    }


}
