package android.mobilechallenge;

import android.mobilechallenge.activities.MainActivity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class);

    @Test
    public void onLaunchCheckInitialValues() {
        onView(withId(R.id.currency_name_spinner)).check(matches(withSpinnerText(containsString("AUD"))));
        onView(withId(R.id.converted_recycler_view)).check(matches(hasDescendant(withText("CAD"))));
        onView(withId(R.id.converted_recycler_view)).check(matches(hasDescendant(withText("MXN"))));
        onView(withId(R.id.converted_recycler_view)).check(matches(hasDescendant(withText("JPY"))));
        onView(withId(R.id.converted_recycler_view)).check(matches(hasDescendant(withText("-"))));
    }

    @Test
    public void onInputValidDataCheckConvertedValues() {
        onView(withId(R.id.currency_value)).perform(replaceText("50.00"));
        onView(withId(R.id.currency_name_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CAD"))).perform(click());
    }

    @Test
    public void onInputZeroValueCheckConvertedValues() {
        onView(withId(R.id.currency_value)).perform(replaceText("0"));
        onView(withId(R.id.converted_recycler_view)).check(matches(hasDescendant(withText("AUD"))));
    }
}

