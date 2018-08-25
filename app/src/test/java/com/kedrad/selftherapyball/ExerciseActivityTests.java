package com.kedrad.selftherapyball;
import android.content.Context;
import android.content.res.Resources;
import android.test.ActivityTestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class ExerciseActivityTests extends ActivityTestCase {

    @Mock
    Context mMockContext;

    @Test
    public void testObtainImages_withWrongName(){
        String selectedPlan = "1234";
        int muscleId = 1;
        ExerciseActivity exerciseActivity = new ExerciseActivity();

        when(mMockContext.getResources()).thenReturn(getInstrumentation().getContext().getResources());

        boolean result = exerciseActivity.obtainImages(selectedPlan, muscleId, mMockContext);
        assertThat(result, is(false) );
    }

    @Test
    public void testObtainImages_withWrongId(){
        String selectedPlan = "neck";
        int muscleId = 25;
        ExerciseActivity exerciseActivity = new ExerciseActivity();

        when(mMockContext.getResources()).thenReturn(getInstrumentation().getContext().getResources());

        boolean result = exerciseActivity.obtainImages(selectedPlan, muscleId, mMockContext);
        assertThat(result, is(false) );
    }

    @Test
    public void testObtainImages_withRightNameAndId(){
        String selectedPlan = "neck";
        int muscleId = 2;
        ExerciseActivity exerciseActivity = new ExerciseActivity();

        when(mMockContext.getResources()).thenReturn(getInstrumentation().getContext().getResources());

        boolean result = exerciseActivity.obtainImages(selectedPlan, muscleId, mMockContext);
        assertThat(result, is(true) );
    }
}
