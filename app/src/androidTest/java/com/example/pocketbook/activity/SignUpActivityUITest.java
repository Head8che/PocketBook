package com.example.pocketbook.activity;

import android.os.SystemClock;
import android.view.View;
import androidx.test.rule.ActivityTestRule;
import com.example.pocketbook.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

public class SignUpActivityUITest {

    @Rule
    public ActivityTestRule<SignUpActivity> Activity = new ActivityTestRule<SignUpActivity>(SignUpActivity.class);

    private SignUpActivity currentActivity = null;
    @Before
    public void setUp() throws Exception {
        currentActivity = Activity.getActivity();
    }


    @Test
    public void launchTest() {
        View passwordField = currentActivity.findViewById(R.id.PasswordReg);
        assertNotNull(passwordField);
    }


    @Test
    public void SignUpTest(){
        SystemClock.sleep(800);
        assertNotNull(currentActivity.findViewById(R.id.RegisterConfirm));
        onView(withId(R.id.firstName)).perform(typeText("David")).perform(closeSoftKeyboard());
        onView(withId(R.id.lastName)).perform(typeText("Beckham")).perform(closeSoftKeyboard());
        onView(withId(R.id.userName)).perform(typeText("Beckham210")).perform(closeSoftKeyboard());
        onView(withId(R.id.EmailReg)).perform(typeText("david@gamil.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.PasswordReg)).perform(typeText("Beckham@310")).perform(closeSoftKeyboard());
    }
}
