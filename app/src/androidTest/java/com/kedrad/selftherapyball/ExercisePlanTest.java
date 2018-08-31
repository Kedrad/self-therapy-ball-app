package com.kedrad.selftherapyball;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;


import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.CoreMatchers.anything;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


/*
 *Tests for the ExercisePlanActivity that shows the exercise plan and lets user start the exercise or see the ball location
 */

@RunWith(AndroidJUnit4.class)
public class ExercisePlanTest {
    @Rule
    public IntentsTestRule<ExercisePlanActivity> mExercisePlanActivityTestRule =
            new IntentsTestRule<>(ExercisePlanActivity.class);


    @Test
    public void selectItemInTheExercisesListView_ShowsBallLocation() throws Exception {

        int muscleId = 1;

        //Select a row in the list view
        onData(anything()).inAdapterView(withId(R.id.listViewExercises)).atPosition(muscleId).perform(click());

        //Check if Ball Location Activity is launched
        IdlingRegistry.getInstance().register(new WaitActivityIsResumedIdlingResource(BallLocationActivity.class.getName()));
        intended(hasComponent(BallLocationActivity.class.getName()));

    }

    @Test
    public void clickFAB_startsExerciseActivity() throws Exception {

        //Click on the start Exercise Activity FAB
        onView(withId(R.id.fab)).perform(click());

        //Check if Exercise Activity is launched
        IdlingRegistry.getInstance().register(new WaitActivityIsResumedIdlingResource(ExerciseActivity.class.getName()));
        intended(hasComponent(ExerciseActivity.class.getName()));
    }


}

