package com.kedrad.selftherapyball;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;


import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.allOf;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/*
 *Tests for the MainActivity that handles choosing the exercise plan
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void selectItemInTheMenuListView_launchesActivityCorrectly() throws Exception {
        //Checks if clicking item in the menu list view starts the correct activity
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        int exercisePlanId = 2;
        String exercisePlanName = resources.getStringArray(R.array.menu_pain)[exercisePlanId];

        onData(anything()).inAdapterView(withId(R.id.listViewMenu)).atPosition(exercisePlanId).perform(click());

        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(exercisePlanName)));
    }

}
