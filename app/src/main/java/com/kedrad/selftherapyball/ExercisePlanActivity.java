package com.kedrad.selftherapyball;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kogitune.activity_transition.ActivityTransition;

import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.Button;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.Shape;

public class ExercisePlanActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iv_background) ImageView imageViewBackground;
    @BindView(R.id.listViewExercises) ListView listViewExercises;
    @BindView(R.id.fab) FloatingActionButton fab;

    int selectedPlanId; //ID of selected exercise plan from the menu activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_plan);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting string array of the menu items for setting a title in the toolbar
        String[] titles = getResources().getStringArray(R.array.menu_pain);

        //Loading image into the background
        imageViewBackground = findViewById(R.id.iv_background);
        GlideApp.with(this).load(R.drawable.front_plan_activity).fitCenter().into(imageViewBackground);

        selectedPlanId = getIntent().getIntExtra(MainActivity.SELECTED_PLAN_ID, 0);

        //Loading array of strings from resources into the ListView
        listViewExercises = findViewById(R.id.listViewExercises);
        String[] exerciseslistArray;

        switch (selectedPlanId){
            case 0:
                this.setTitle(titles[0]);
                exerciseslistArray = getResources().getStringArray(R.array.neck);
                break;
            case 1:
                this.setTitle(titles[1]);
                exerciseslistArray = getResources().getStringArray(R.array.shoulder);
                break;
            case 2:
                this.setTitle(titles[2]);
                exerciseslistArray = getResources().getStringArray(R.array.thoracic_spine);
                break;
            case 3:
                this.setTitle(titles[3]);
                exerciseslistArray = getResources().getStringArray(R.array.lumbar_spine);
                break;
            case 4:
                this.setTitle(titles[4]);
                exerciseslistArray = getResources().getStringArray(R.array.hips);
                break;
            case 5:
                this.setTitle(titles[5]);
                exerciseslistArray = getResources().getStringArray(R.array.knee);
                break;
            case 6:
                this.setTitle(titles[6]);
                exerciseslistArray = getResources().getStringArray(R.array.achilles_and_ankle_joint);
                break;
            default:
                this.setTitle(titles[0]);
                exerciseslistArray = getResources().getStringArray(R.array.neck);
                break;
        }

        listViewExercises.setAdapter(new ExercisePlanListAdapter(this, exerciseslistArray));

        //Setting OnItemClickListner for the list
        listViewExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ExercisePlanActivity.this, BallLocationActivity.class);

                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, position);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                startActivity(intent);
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExercisePlanActivity.this, ExerciseActivity.class);
                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, 0);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                startActivity(intent);
                finish();
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
            Intent intent = new Intent(ExercisePlanActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.STARTED_FROM_SHOWCASE, true);
            //Starting new activity
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showShowcase() {
        View showcaseViewList = findViewById(R.id.showcase_view_list);
        View showcaseViewListItem = findViewById(R.id.showcase_view_list_item);

        //Listener for GOT IT button
        IShowcaseListener listener = new IShowcaseListener() {
            @Override
            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

            }

            @Override
            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                //Clicking GOT IT button mocks performing a click on the first item in the list view
                // and informs the next activity that it was started from the showcase
                Intent intent = new Intent(ExercisePlanActivity.this, BallLocationActivity.class);
                intent.putExtra(MainActivity.SELECTED_MUSCLE_ID, 0);
                intent.putExtra(MainActivity.SELECTED_PLAN_ID, selectedPlanId);
                intent.putExtra(MainActivity.STARTED_FROM_SHOWCASE, true);
                //Starting new activity
                startActivity(intent);
            }
        };

        MaterialShowcaseView.Builder firstShowcaseView = new MaterialShowcaseView.Builder(this)
                .setTarget(showcaseViewList)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_plan_activity))
                .setDelay(500)
                ;

        MaterialShowcaseView.Builder secondShowcaseView = new MaterialShowcaseView.Builder(this)
                .setTarget(showcaseViewListItem)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_exercise_plan_activity_1))
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
