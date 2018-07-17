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

import carbon.widget.Button;

public class ExercisePlanActivity extends AppCompatActivity {

    //ID of selected exercise plan from the menu activity
    int selectedPlanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting title in the toolbar
        this.setTitle(R.string.exercise_plan_activity_title);

        //Loading image into the background
        ImageView imageViewBackground = findViewById(R.id.iv_background);
        GlideApp.with(this).load(R.drawable.front_blurred).fitCenter().into(imageViewBackground);

        selectedPlanId = getIntent().getIntExtra("selectedItem", 0);

        //Loading array of strings from resources into the ListView
        ListView listViewExercises = findViewById(R.id.listViewExercises);
        String[] exerciseslistArray;

        switch (selectedPlanId){
            case 0:
                exerciseslistArray = getResources().getStringArray(R.array.neck);
                break;
            case 1:
                exerciseslistArray = getResources().getStringArray(R.array.shoulder);
                break;
            case 2:
                exerciseslistArray = getResources().getStringArray(R.array.thoracic_spine);
                break;
            case 3:
                exerciseslistArray = getResources().getStringArray(R.array.lumbar_spine);
                break;
            case 4:
                exerciseslistArray = getResources().getStringArray(R.array.hips);
                break;
            case 5:
                exerciseslistArray = getResources().getStringArray(R.array.knee);
                break;
            case 6:
                exerciseslistArray = getResources().getStringArray(R.array.achilles_and_ankle_joint);
                break;
            default:
                exerciseslistArray = getResources().getStringArray(R.array.neck);
                break;
        }

        listViewExercises.setAdapter(new ArrayAdapter<String>(
                ExercisePlanActivity.this,
                R.layout.listview_white_item,
                exerciseslistArray));

        //Setting OnItemClickListner for the list
        listViewExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ExercisePlanActivity.this, BallLocationActivity.class);
                int selectedItem = position;
                intent.putExtra("selectedItem", selectedItem);
                startActivity(intent);
            }
        });

        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExercisePlanActivity.this, ExerciseActivity.class);

                startActivity(intent);
            }
        });

    }

}
