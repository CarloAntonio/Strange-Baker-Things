package com.riskitbiskit.strangebakerthings;

import android.app.Activity;
import android.support.test.runner.AndroidJUnit4;

import com.riskitbiskit.strangebakerthings.MainActivityFiles.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.app.Instrumentation.ActivityResult;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivity = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllInternalIntents() {
        intending(isInternal()).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickGridViewItem_Opens_RecipeDetails() {
        onData(anything()).inAdapterView(withId(R.id.recipe_list_gv)).atPosition(0).perform(click());

        intended(
                hasExtra(MainActivity.RECIPE_INDEX_NUMBER, 0)
        );
    }
}
