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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.Button;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class BallLocationActivity extends AppCompatActivity {

    //ID of selected exercise plan from the menu activity
    int selectedPlanId;
    //ID of selected muscle from the list in the exercise plan
    int selectedMuscleId;

    static final String BALL_LOCATION_IMAGES_ARRAY_SUFFIX = "_ball_location_images";
    static final String BALL_LOCATION_COORDINATES_ARRAY_SUFFIX = "_ball_location_coordinates";

    @BindView(R.id.left_guideline) Guideline leftGuideline;
    @BindView(R.id.right_guideline) Guideline rightGuideline;
    @BindView(R.id.top_guideline) Guideline topGuideline;
    @BindView(R.id.bottom_guideline) Guideline bottomGuideline;
    @BindView(R.id.iv_background) ImageView imageViewBackground;
    @BindView(R.id.pulsator) PulsatorLayout pulsator;
    @BindView(R.id.fab) FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball_location);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Receiving selected plan ID from the menu activity
        selectedPlanId = getIntent().getIntExtra(MainActivity.SELECTED_PLAN_ID, 0);

        //Receiving selected muuscle ID from the precious activity
        selectedMuscleId = getIntent().getIntExtra(MainActivity.SELECTED_MUSCLE_ID, 0);

        setBackgroundImage();
        placeBallLocationIndicator();

        //Setting title in the toolbar
        setTitle(R.string.title_activity_ball_location);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BallLocationActivity.this, ExerciseActivity.class);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, selectedMuscleId);
                finish();
                startActivity(intent);

            }
        });

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
            Intent intent = new Intent(BallLocationActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.STARTED_FROM_SHOWCASE, true);
            //Starting new activity
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setBackgroundImage(){
        int backgroundDrawablesArrayId = this.getResources().
                getIdentifier(getResources().getStringArray(R.array.main_menu_items)[selectedPlanId] + BALL_LOCATION_IMAGES_ARRAY_SUFFIX, "array", this.getPackageName());

        //Loading the appropriate background image
        TypedArray drawablesTypedArray = getResources().obtainTypedArray(backgroundDrawablesArrayId);
        GlideApp.with(this).load(drawablesTypedArray.getDrawable(selectedMuscleId)).fitCenter().into(imageViewBackground);
        drawablesTypedArray.recycle();
    }

    private void placeBallLocationIndicator(){
        //Views used for placing the ball location indicator


        int ballLocationArrayId = this.getResources().
                getIdentifier(getResources().getStringArray(R.array.main_menu_items)[selectedPlanId] + BALL_LOCATION_COORDINATES_ARRAY_SUFFIX, "array", this.getPackageName());

        String[] ballLocationStringArray = getResources().getStringArray(ballLocationArrayId);

        //Splitting string obtained from the ball_location_coordinates.xml file; left, right, top and bottom percentages are divided with spaces
        String[] ballLocationCoordinates = ballLocationStringArray[selectedMuscleId].trim().split("\\s+");

        leftGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[0]));
        rightGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[1]));
        topGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[2]));
        bottomGuideline.setGuidelinePercent(Float.valueOf(ballLocationCoordinates[3]));

        //Starting the pulsating indicator
        pulsator.start();

    }


    public void showShowcase() {
        View showcaseViewListItem = findViewById(R.id.showcase_view_list_item);

        //Listener for GOT IT button
        IShowcaseListener listener = new IShowcaseListener() {
            @Override
            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

            }

            @Override
            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                //Clicking GOT IT button mocks performing a click on the fab
                // and informs the next activity that it was started from the showcase
                Intent intent = new Intent(BallLocationActivity.this, ExerciseActivity.class);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, selectedMuscleId);
                intent.putExtra(MainActivity.STARTED_FROM_SHOWCASE, true);
                //Starting new activity
                startActivity(intent);
            }
        };

        MaterialShowcaseView.Builder firstShowcaseView = new MaterialShowcaseView.Builder(this)
                .setTarget(pulsator)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_ball_location_activity))
                .setDelay(500)
                ;

        MaterialShowcaseView.Builder secondShowcaseView = new MaterialShowcaseView.Builder(this)
                .setTarget(fab)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_ball_location_activity_1))
                .setListener(listener)
                ;

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);

        sequence.setConfig(config);

        sequence.addSequenceItem(firstShowcaseView.build());

        sequence.addSequenceItem(secondShowcaseView.build());


        sequence.start();

    }

}
