package com.kedrad.selftherapyball;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends AppCompatActivity {
    public static final String SELECTED_PLAN_ID = "SELECTED_PLAN_ID";
    public static final String SELECTED_MUSCLE_ID = "SELECTED_MUSCLE_ID";
    public static final String STARTED_FROM_SHOWCASE = "STARTED_FROM_SHOWCASE";
    public static final String COMPLETED_FIRST_LAUNCH_SHOWCASE_PREF_NAME = "COMPLETED_FIRST_LAUNCH_SHOWCASE";

    @BindView(R.id.listViewMenu) ListView listViewMenu;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        //Setting title in the toolbar
        this.setTitle(R.string.menu_message);


        //Filling menu list with content using custom ArrayAdapter
        listViewMenu = findViewById(R.id.listViewMenu);
        TypedArray images = getResources().obtainTypedArray(R.array.menu_images);
        
        String[] names = getResources().getStringArray(R.array.menu_pain);
        String[] durations = getResources().getStringArray(R.array.menu_durations);
        listViewMenu.setAdapter(new MenuListAdapter(this, images, names, durations));

        //Setting OnItemClickListener for the list
        listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, ExercisePlanActivity.class);
                int selectedItem = position;
                intent.putExtra(SELECTED_PLAN_ID, selectedItem);

                //Starting new activity
                startActivity(intent);
            }
        });


        //Show the next showcase when activity was started from the showcase in the previous activity
        if(getIntent().getBooleanExtra(MainActivity.STARTED_FROM_SHOWCASE, false))
            showShowcase();

        // Check if we need to display first launch showcase
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(
                COMPLETED_FIRST_LAUNCH_SHOWCASE_PREF_NAME, false)) {
            // The user hasn't seen the showcase yet, so show it
            showShowcase();
        }

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
            showShowcase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showShowcase() {


        View startTutorialView = findViewById(R.id.start_showcase_view);

        //Listener for GOT IT button
        IShowcaseListener listener = new IShowcaseListener() {
            @Override
            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

            }

            @Override
            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                //Clicking GOT IT button mocks performing a click on the first item in the list view
                // and informs the next activity that it was started from the showcase
                Intent intent = new Intent(MainActivity.this, ExercisePlanActivity.class);

                int selectedItem = 0;
                intent.putExtra(SELECTED_PLAN_ID, selectedItem);
                intent.putExtra(STARTED_FROM_SHOWCASE, true);
                //Starting new activity
                startActivity(intent);
            }
        };

        new MaterialShowcaseView.Builder(this)
                .setTarget(startTutorialView)
                .setDismissText(getResources().getString(R.string.showcase_got_it_text))
                .setContentText(getResources().getString(R.string.showcase_text_main_activity))
                .setDelay(500)
                .setListener(listener)
                .show();

    }

}
