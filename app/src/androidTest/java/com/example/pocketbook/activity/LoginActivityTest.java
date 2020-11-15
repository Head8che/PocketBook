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

// TODO; actually log in and go to HomeFragment
// TODO: Handle creating MockUser with Auth and everything in FirebaseIntegrity
// TODO: Rename LogIn to Login

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> Activity = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity currentActivity = null;
    @Before
    public void setUp() {
        currentActivity = Activity.getActivity();
    }

    // TODO: Check Sign Up button sends to SignUpActivity
    // TODO: Check invalid email form (should not click login)
    // TODO: Check invalid password form (should not click login)
    // TODO: Check login with invalid account (timestamp for randomness)
    // TODO: Create valid account, logout, check login with valid account


    @Test
    public void launchTest() {
        View emailField = currentActivity.findViewById(R.id.UserReg);
        assertNotNull(emailField);
        View passwordField = currentActivity.findViewById(R.id.PasswordReg);
        assertNotNull(passwordField);
    }

    @Test
    public void LogInTest(){
        SystemClock.sleep(800);
        assertNotNull(currentActivity.findViewById(R.id.LoginBtn));
        onView(withId(R.id.UserReg)).perform(typeText("jane@gmail.com")).perform(closeSoftKeyboard());
        onView(withId(R.id.PasswordReg)).perform(typeText("123456")).perform(closeSoftKeyboard());
    }

}


