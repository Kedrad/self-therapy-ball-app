package com.kedrad.selftherapyball;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kogitune.activity_transition.ActivityTransition;

import carbon.widget.Button;

public class ExercisePlanActivity extends AppCompatActivity {

    int selectedPlanId; //ID of selected exercise plan from the menu activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting string array of the menu items for setting a title in the toolbar
        String[] titles = getResources().getStringArray(R.array.menu_pain);

        //Loading image into the background
        ImageView imageViewBackground = findViewById(R.id.iv_background);
        GlideApp.with(this).load(R.drawable.front_plan_activity).fitCenter().into(imageViewBackground);

        selectedPlanId = getIntent().getIntExtra("selectedItem", 0);

        //Loading array of strings from resources into the ListView
        ListView listViewExercises = findViewById(R.id.listViewExercises);
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

                intent.putExtra("selectedMuscle", position);
                intent.putExtra("selectedPlan", selectedPlanId);
                startActivity(intent);
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExercisePlanActivity.this, ExerciseActivity.class);
                intent.putExtra("selectedMuscle", 0);
                intent.putExtra("selectedPlan", selectedPlanId);
                startActivity(intent);
            }
        });

    }

}
