package com.kedrad.selftherapyball;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.ohoussein.playpause.PlayPauseView;

public class ExerciseActivity extends AppCompatActivity {

    private boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final PlayPauseView view = (PlayPauseView) findViewById(R.id.play_pause_view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.toggle();
                ImageView animatedView = (ImageView) findViewById(R.id.iv_movement_direction);
                isChecked = !isChecked;
                final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
                animatedView.setImageState(stateSet, true);

            }
        });
    }

}
