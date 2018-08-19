package com.kedrad.selftherapyball;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.app.Activity;
import android.support.constraint.Guideline;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import carbon.widget.Button;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class BallLocationActivity extends AppCompatActivity {

    //ID of selected exercise plan from the menu activity
    int selectedPlanId;
    //ID of selected muscle from the list in the exercise plan
    int selectedMuscleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Receiving selected plan ID from the menu activity
        selectedPlanId = getIntent().getIntExtra("selectedPlan", 0);

        //Receiving selected muuscle ID from the precious activity
        selectedMuscleId = getIntent().getIntExtra("selectedMuscle", 0);

        setBackgroundImage();
        placeBallLocationIndicator();

        //Setting title in the toolbar
        setTitle(R.string.title_activity_ball_location);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BallLocationActivity.this, ExerciseActivity.class);
                intent.putExtra("selectedPlan", selectedPlanId);
                intent.putExtra("selectedMuscle", selectedMuscleId);
                finish();
                startActivity(intent);

            }
        });
    }


    private void setBackgroundImage(){
        int backgroundDrawablesArrayId = this.getResources().
                getIdentifier(getResources().getStringArray(R.array.main_menu_items)[selectedPlanId] + "_ball_location_images", "array", this.getPackageName());

        //Loading the appropriate background image
        ImageView imageViewBackground = findViewById(R.id.iv_background);
        TypedArray drawablesTypedArray = getResources().obtainTypedArray(backgroundDrawablesArrayId);
        GlideApp.with(this).load(drawablesTypedArray.getDrawable(selectedMuscleId)).fitCenter().into(imageViewBackground);
        drawablesTypedArray.recycle();
    }

    private void placeBallLocationIndicator(){
        //Views used for placing the ball location indicator
        Guideline leftGuideline = findViewById(R.id.left_guideline);
        Guideline rightGuideline = findViewById(R.id.right_guideline);
        Guideline topGuideline = findViewById(R.id.top_guideline);
        Guideline bottomGuideline = findViewById(R.id.bottom_guideline);

        int ballLocationArrayId = this.getResources().
                getIdentifier(getResources().getStringArray(R.array.main_menu_items)[selectedPlanId] + "_ball_location_coordinates", "array", this.getPackageName());

        String[] ballLocationStringArray = getResources().getStringArray(ballLocationArrayId);

        //Splitting string obtained from the ball_location_coordinates.xml file; left, right, top and bottom percentages are divided with spaces
        String[] ballLocationCoordinates = ballLocationStringArray[selectedMuscleId].trim().split("\\s+");

        leftGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[0]));
        rightGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[1]));
        topGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[2]));
        bottomGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[3]));

        //Starting the pulsating indicator
        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();

        //Placing the start button above or below the indicator depending on its location to improve visibility

    }

}
