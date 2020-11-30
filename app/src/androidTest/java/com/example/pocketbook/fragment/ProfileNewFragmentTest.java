package com.example.pocketbook.fragment;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.EditProfileActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.activity.SignUpActivity;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProfileNewFragmentTest {

    private Solo solo;
    private boolean inEditProfileActivityWithChanges = false;
    private long currentTime = System.currentTimeMillis();

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    /**
     * Runs before all tests and signs out any logged in user.
     */
    @BeforeClass
    public static void signOut() {
        FirebaseIntegrity.signOutCurrentlyLoggedInUser();
    }

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnView(solo.getView(R.id.loginSignUpBtn));  // click on sign up button

        // Asserts that the current activity is SignUpActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.sleep(2000); // give it time to change activity

        //////////////////////////////// CREATE A MOCK USER ACCOUNT ////////////////////////////////

        View signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        TextInputEditText firstNameField = (TextInputEditText)
                solo.getView(R.id.signUpFirstNameField);
        TextInputEditText lastNameField = (TextInputEditText)
                solo.getView(R.id.signUpLastNameField);
        TextInputEditText usernameField = (TextInputEditText)
                solo.getView(R.id.signUpUsernameField);
        TextInputEditText emailField = (TextInputEditText)
                solo.getView(R.id.signUpEmailField);
        TextInputEditText passwordField = (TextInputEditText)
                solo.getView(R.id.signUpPasswordField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        solo.enterText(lastNameField, "MockLast");  // add a lastName

        assertNotNull(usernameField);  // username field exists
        solo.enterText(usernameField, "MockUsername");  // add a username

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, "mockeditprofile" + currentTime + "@gmail.com"); //add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, "123456");  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        ////////////////////////////// SKIP ONBOARDING INSTRUCTIONS ////////////////////////////////

        View skipBtn = skipBtn = solo.getView(R.id.onBoardingActivitySkipBtn);
        solo.clickOnView(skipBtn);

        solo.sleep(2000); // give it time to change activity

//        //////////////////////////////// GO TO EditProfileActivity /////////////////////////////////

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.bottom_nav_profile));  // click on profile button

        // gets the recycler for the books
        RecyclerView view = (RecyclerView) solo.getView(R.id.profileNewRecyclerBooks);

        int position = 1;

        onView(withId(R.id.profileNewRecyclerBooks))  // click on the mock book
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
        solo.sleep(2000); // give it time to change activity
    }


    /**
     * Check if the cancel button redirects to HomeActivity with assertCurrentActivity
     * Check if the fragment after redirect to ProfileFragment with assertTrue
     * Check if the selected bottom navigation item is correct with assertEquals
     */
    @Test
    public void checkBackButton() {
        View backBtn = solo.getView(R.id.viewBookFragBackBtn);

        solo.clickOnView(backBtn); // click cancel button

        solo.sleep(2000); // give it time to change activity

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        BottomNavigationView bottomNavigation = (BottomNavigationView)
                solo.getView(R.id.bottomNavigationView);

        // assert that we are in ProfileFragment i.e. that the user's first name and last name
        // are shown, and that Suggested Books is shown, since the user has no books
        assertTrue(solo.searchText("MockFirst"));
        assertTrue(solo.searchText("MockLast"));
        assertTrue(solo.searchText("Suggested Books"));

        // assert that the profile bottom navigation item is currently selected
        assertEquals(R.id.bottom_nav_profile, bottomNavigation.getSelectedItemId());

        inEditProfileActivityWithChanges = false;
    }

    /**
     * Runs after each test to exit from EditProfileActivity
     * and remove the test user from Firebase.
     */
    @After
    public void removeMockFromFirebase() {

        // Exiting the test from EditProfileActivity, without going back to ProfileFragment,
        // causes errors because ProfileFragment gets closed incorrectly by the test.

        if (inEditProfileActivityWithChanges) {

            View cancelBtn = solo.getView(R.id.editProfileCancelBtn);

            solo.clickOnView(cancelBtn); // click cancel button

            // Asserts that the current activity is EditProfileActivity (i.e. no redirect).
            solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

            // True if the text 'Discard Changes?' is present
            assertTrue(solo.searchText("Discard Changes?"));

            solo.clickOnText("DISCARD"); // click discard text

            // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
            solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        }

        FirebaseIntegrity.deleteCurrentlyLoggedInUser();
    }
}
