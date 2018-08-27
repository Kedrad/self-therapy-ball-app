package com.kedrad.selftherapyball;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.kogitune.activity_transition.ActivityTransition;
import com.ohoussein.playpause.PlayPauseView;

import java.util.Timer;
import java.util.TimerTask;

import static com.kedrad.selftherapyball.MainActivity.COMPLETED_FIRST_LAUNCH_SHOWCASE_PREF_NAME;

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

    //Suffixes for xml resources arrays
    static final String EXERCISE_IMAGES_ARRAY_SUFFIX = "_images";
    static final String EXERCISE_IMAGES_AFTER_30_SECONDS_ARRAY_SUFFIX = "_images_after_30_seconds";
    static final String BALL_LOCATION_ARRAY_SUFFIX = "_ball_location";
    static final String MOVEMENT_DIRECTION_ARRAY_SUFFIX = "_movement_direction";
    static final String MOVEMENT_DIRECTION_AFTER_30_SECONDS_ARRAY_SUFFIX = "_movement_direction_after_30_seconds";

    @BindView(R.id.timer) TextView tvTimer;
    @BindView(R.id.image_switcher_instruction) ImageSwitcher imageSwitcher;
    @BindView(R.id.progress_bar) CircularProgressBar progressBar;
    @BindView(R.id.play_pause_view) PlayPauseView playPauseView;
    @BindView(R.id.tv_ball_location) TextView tvBallLocation;
    @BindView(R.id.tv_movement_direction) TextView tvMovementDirection;
    @BindView(R.id.iv_movement_direction) ImageView ivMovementDirection;
    @BindView(R.id.fab_previous_exercise) FloatingActionButton fabPreviousExercise;
    @BindView(R.id.fab_next_exercise) FloatingActionButton fabNextExercise;
    @BindView(R.id.btn_ball_location) Button btnBallLocation;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Receiving selected plan ID from the menu activity
        selectedPlanId = getIntent().getIntExtra(MainActivity.SELECTED_PLAN_ID, 0);

        //Receiving selected muuscle ID from the previous activity
        selectedMuscleId = getIntent().getIntExtra(MainActivity.SELECTED_MUSCLE_ID, 0);
        selectedPlanName = getResources().getStringArray(R.array.main_menu_items)[selectedPlanId];

        obtainImages(selectedPlanName, selectedMuscleId, this);

        //Instantiating a slideshow on top of the activity that shows how to perform the exercise
        slideShow = new SlideShow(imageSwitcher, instructionImages, instructionImagesAfter30Seconds, this);


        //Setting the title to selected muscle name
        setTitle(getResources().getStringArray(this.getResources().
                getIdentifier(selectedPlanName , "array", this.getPackageName()))[selectedMuscleId]);

        prepareViews();

        setOnClickListeners();

        //Show the next showcase when activity was started from the showcase in the previous activity
        if(getIntent().getBooleanExtra(MainActivity.STARTED_FROM_SHOWCASE, false))
            showShowcase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_how_to) {
            //Start the app showcase
            Intent intent = new Intent(ExerciseActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.STARTED_FROM_SHOWCASE, true);
            //Starting new activity
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isCountdownRunning) {
            pauseCountdown();
            playPauseView.toggle();
            isCountdownRunning = !isCountdownRunning;
        }

        slideShow.stop();
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

                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, selectedMuscleId - 1);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                startActivity(intent);
                finish();
            }
        });

        fabNextExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, ExerciseActivity.class);

                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, selectedMuscleId + 1);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                startActivity(intent);
                finish();
            }
        });

        //When ball location button is clicked, start the ball location activity
        btnBallLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, BallLocationActivity.class);

                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, selectedMuscleId);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                startActivity(intent);
            }
        });
    }

    public boolean obtainImages(String selectedPlan, int muscleId, Context context){
        //Obtaining images array from the xml resources
        int instructionImagesArrayId = context.getResources().
                getIdentifier(selectedPlan + EXERCISE_IMAGES_ARRAY_SUFFIX, "array", context.getPackageName());
        int instructionImagesAfter30SecondsArrayId = context.getResources().
                getIdentifier(selectedPlan + EXERCISE_IMAGES_AFTER_30_SECONDS_ARRAY_SUFFIX, "array", context.getPackageName());

        instructionImages = getResources().getStringArray(instructionImagesArrayId)[muscleId].trim().split("\\s+");
        instructionImagesAfter30Seconds = getResources().getStringArray(instructionImagesAfter30SecondsArrayId)[muscleId].trim().split("\\s+");

        return (instructionImages != null);
    }

    private void prepareViews(){
        //Obtaining string arrays from the xml resources
        int ballLocationArrayId = this.getResources().
                getIdentifier(selectedPlanName + BALL_LOCATION_ARRAY_SUFFIX, "array", this.getPackageName());
        int movementDirectionArrayId = this.getResources().
                getIdentifier(selectedPlanName + MOVEMENT_DIRECTION_ARRAY_SUFFIX, "array", this.getPackageName());
        int movementDirectionAfter30SecondsArrayId = this.getResources().
                getIdentifier(selectedPlanName + MOVEMENT_DIRECTION_AFTER_30_SECONDS_ARRAY_SUFFIX, "array", this.getPackageName());

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

        //Keep the screen on during exercise
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textViewTimer = new CountDownTimer(duration, 1000) {

            public void onTick(long millisUntilFinished) {
                //Stop the countdown when user left the activity
                if(activity.isFinishing() )
                    this.cancel();

                millisRemaining = millisUntilFinished;

                tvTimer.setText(Long.toString(millisUntilFinished / 1000));

                if((millisUntilFinished / 1000) == 30){
                    changeDirection();
                }
            }


            public void onFinish() {
                vibrate();
                playSound(2);

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
                //Stop keeping the screen on
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }.start();

    }

    public void pauseCountdown(){
        textViewTimer.cancel();
        progressBarTimer.cancel();
        //Stop keeping the screen on
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void changeDirection(){
        vibrate();
        playSound(1);

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

    public void playSound(int numberOfTones){
        MediaPlayer mp;

        if(numberOfTones == 1)
            mp = MediaPlayer.create(this, R.raw.one_tone);
        else
            mp = MediaPlayer.create(this, R.raw.two_tones);

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


    public void showShowcase() {
        LinearLayout navigationButtonsView = findViewById(R.id.navigation_buttons_view);
        LinearLayout movementDirectionView = findViewById(R.id.movement_direction_view);
        View menuItemHowTo = findViewById(R.id.showcase_view_menu);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);

        sequence.setConfig(config);

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(imageSwitcher)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity))
                .setDelay(500)
                .withRectangleShape()
                .build()
        );

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(progressBar)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity_1))
                .setDelay(500)
                .build()
        );

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(navigationButtonsView)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity_2))
                .setDelay(500)
                .build()
        );

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(btnBallLocation)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity_3))
                .setDelay(500)
                .build()
        );

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(movementDirectionView)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity_4))
                .setDelay(500)
                .withRectangleShape()
                .build()
        );

        IShowcaseListener listenerShowcase6 = new IShowcaseListener() {
            @Override
            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                //Imitate changing direction after 30 seconds
                playPauseView.toggle();
                tvTimer.setText("30");
                progressBar.setProgress(30000f);
                vibrate();
                playSound(1);
                animateMovementDirectionIcon();
                tvMovementDirection.setText(movementDirectionTexts[1]);
            }

            @Override
            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

            }
        };

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(progressBar)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity_5))
                .setDelay(500)
                .setListener(listenerShowcase6)
                .build()
        );

        IShowcaseListener listenerShowcase7 = new IShowcaseListener() {
            @Override
            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                //Imitate finishing the exercise
                playPauseView.toggle();
                tvTimer.setText("0");
                progressBar.setProgress(60000f);
                playSound(2);
                vibrate();
            }

            @Override
            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

            }
        };

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(fabNextExercise)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity_6))
                .setDelay(500)
                .setListener(listenerShowcase7)
                .build()
        );

        IShowcaseListener listenerShowcase8 = new IShowcaseListener() {
            @Override
            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

            }

            @Override
            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                //Set the boolean in the preferences so the user won't see the showcase on the next lunch
                SharedPreferences.Editor sharedPreferencesEditor =
                        PreferenceManager.getDefaultSharedPreferences(ExerciseActivity.this).edit();
                sharedPreferencesEditor.putBoolean(
                        COMPLETED_FIRST_LAUNCH_SHOWCASE_PREF_NAME, true);
                sharedPreferencesEditor.apply();

                //Clicking GOT IT button finishes the showcase and starts the main activity
                Intent intent = new Intent(ExerciseActivity.this, MainActivity.class);
                //Starting new activity
                startActivity(intent);
            }
        };

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(menuItemHowTo)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text_end))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_activity_7))
                .setDelay(500)
                .setListener(listenerShowcase8)
                .build()
        );

        sequence.start();

    }

}
