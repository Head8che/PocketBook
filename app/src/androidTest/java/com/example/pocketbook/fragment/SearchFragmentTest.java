package com.example.pocketbook.fragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.AddBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.activity.LocationActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.activity.SignUpActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class SearchFragmentTest {
    /**
     *  Testing that SearchView Exists and text can typed in it
     *  Testing that switching tabs works
     */

    private Solo solo;
    private User mU; // mock User

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);
    /**
     * Runs before all tests and signs out any logged in user.
     */
    @BeforeClass
    public static void signOut() {
        FirebaseIntegrity.signOutCurrentlyLoggedInUser();
    }

    /**
     * Runs before all tests and creates solo instance. Also navigates to ViewBookFragment.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // Create Mock User
        mU = new User("mock_fn", "mock_ln", "mock@gmail.com", "mock",
                "123456", "0123456789", "none.jpg");
        FirebaseIntegrity.pushNewUserToFirebaseWithURL(mU, "");


        ////////////////////////////// LOGIN ////////////////////////////////
        solo.waitForActivity(LoginActivity.class, 1000);
        solo.assertCurrentActivity("Not Login Activity", LoginActivity.class);

        View signInBtn = solo.getView(R.id.loginLoginBtn);
        TextInputEditText emailField = (TextInputEditText)
                solo.getView(R.id.loginEmailField);
        TextInputEditText  passwordField = (TextInputEditText)
                solo.getView(R.id.loginPasswordField);

        assertNotNull(emailField);  // email field exists
        solo.clearEditText(emailField);
        solo.enterText(emailField, mU.getEmail()); // enter email

        assertNotNull(passwordField);
        solo.clearEditText(passwordField);
        solo.enterText(passwordField, mU.getPassword());  // enter password

        solo.clickOnView(signInBtn); // click login button

        ////////////////////////////// HomeActivity ////////////////////////////////
        solo.waitForActivity(HomeActivity.class, 1000);
        solo.assertCurrentActivity("Not Home Activity", HomeActivity.class);

        ////////////////////////////// SearchFragment ////////////////////////////////
        View searchIcon = solo.getView(R.id.bottom_nav_search);
        solo.clickOnView(searchIcon);
        solo.waitForFragmentByTag("SEARCH_FRAGMENT", 1000);
    }

    @Test
    public void checkSearchFragmentDisplayedCorrectly() throws Exception {
        // checking that the pager/tablayout is showing
        assertEquals(solo.getView(R.id.searchFragPager).getVisibility(), View.VISIBLE);

        // making sure we have the ability to enter text
        onView(withId(R.id.searchView)).perform(typeText("Testint, testing"));

        // making sure we can switch tabs
        onView(withId(R.id.searchFragPager)).perform(swipeLeft());
        solo.sleep(1000);
        onView(withId(R.id.searchFragPager)).perform(swipeRight());

    }

    /**
     * Runs after each test to remove the mock users and the mock book from Firebase.
     */
    @After
    public void removeMockThingsFromFirebase() {
        // remove user
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (Objects.equals(Objects.requireNonNull( // if currently logged in as first user
                    FirebaseAuth.getInstance().getCurrentUser()).getEmail(), mU.getEmail())) {
                FirebaseIntegrity.deleteUserNotificationsFromFirebase(mU.getEmail());
                FirebaseIntegrity.deleteCurrentlyLoggedInUser();
                solo.sleep(5000);  // give it time to complete task
                signOut();  // sign out of the created user account
            }
        }

        // remove book

    }
}
