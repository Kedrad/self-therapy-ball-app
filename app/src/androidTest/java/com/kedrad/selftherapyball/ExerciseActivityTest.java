package com.kedrad.selftherapyball;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;

import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;


import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import java.util.concurrent.CountDownLatch;

/*
 *Tests for the ExerciseActivity that handles the exercise
 */

@RunWith(AndroidJUnit4.class)
public class ExerciseActivityTest {
    @Rule
    public IntentsTestRule<ExerciseActivity> mExerciseActivityTestRule =
            new IntentsTestRule<>(ExerciseActivity.class);

    @Test
    public void clickPlayButton_StartsCountdown() throws Exception {
        //Click on the play button
        onView(withId(R.id.play_pause_view)).perform(click());
        //Check if countdown is running
        assertTrue(mExerciseActivityTestRule.getActivity().isCountdownRunning());
    }

    @Test
    public void clickPauseButton_StopsCountdown() throws Exception {
        //Click on the play button
        onView(withId(R.id.play_pause_view)).perform(click());
        //Check if countdown is running
        assertTrue(mExerciseActivityTestRule.getActivity().isCountdownRunning());

        //Click on the stop button
        onView(withId(R.id.play_pause_view)).perform(click());
        //Check if countdown is stopped
        assertFalse(mExerciseActivityTestRule.getActivity().isCountdownRunning());
    }

    @Test
    public void clickBallLocationButton_StartsBallLocationActivity() throws Exception {

        //Click on the start Ball Location Activity Button
        onView(withId(R.id.btn_ball_location)).perform(click());

        //Check if Ball Location Activity is launched
        IdlingRegistry.getInstance().register(new WaitActivityIsResumedIdlingResource(BallLocationActivity.class.getName()));
        intended(hasComponent(BallLocationActivity.class.getName()));


    }


    @Test
    public void clickNextExerciseFAB_StartsNextExercise() throws Exception {

        int currentSelectedMuscle = mExerciseActivityTestRule.getActivity().getSelectedMuscleId();

        //Click on the start next exercise FAB
        onView(withId(R.id.fab_next_exercise)).perform(click());

        //Check if next exercise is launched
        IdlingRegistry.getInstance().register(new WaitActivityIsResumedIdlingResource(ExerciseActivity.class.getName()));
        intended(hasComponent(ExerciseActivity.class.getName()));

        //Check if it is the next exercise
        assertEquals(currentSelectedMuscle + 1, mExerciseActivityTestRule.getActivity().getSelectedMuscleId());
    }
}
