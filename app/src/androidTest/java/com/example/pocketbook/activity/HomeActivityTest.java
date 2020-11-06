package com.example.pocketbook.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.robotium.solo.Solo;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {
    private Solo solo;
    private User user;


    @Rule
    public ActivityTestRule<HomeActivity> rule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class, true, true) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, HomeActivity.class);
                    result.putExtra("CURRENT_USER", user = new User("mockFirstName",
                            "mockLastName", "mock@mock.com", "mockUsername",
                            "mockPassword", null));
                    return result;
                }
            };

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        solo.sleep(2000);
    }

    /**
     * Runs after each test to remove the test Book from Firebase.
     */
    @After
    public void removeMockFromFirebase() {
        FirebaseIntegrity.removeAuthorFromFirestore("M0cK^U+H0R");
    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkBookCover(){
        View book = solo.getView(R.id.recycler_books);
        solo.clickOnView(book);
        ViewInteraction recyclerView = onView(allOf(withId(R.id.recycler_books), childAtPosition(withId(R.id.container), 0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction bottomNavigationItemView = onView(allOf(withId(R.id.bottom_nav_home), withContentDescription("Home"), childAtPosition(
                childAtPosition(withId(R.id.bottomNavigationView), 0), 0), isDisplayed()));bottomNavigationItemView.perform(click());

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    @Test
    public void checkBookNoCover() {
        View book = solo.getView(R.id.recycler_books);
        solo.clickOnView(book);
        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recycler_books), childAtPosition(withId(R.id.container), 0)));
        recyclerView2.perform(actionOnItemAtPosition(2, click()));
        ViewInteraction bottomNavigationItemView2 = onView(allOf(withId(R.id.bottom_nav_home), withContentDescription("Home"), childAtPosition(
                childAtPosition(withId(R.id.bottomNavigationView), 0), 0), isDisplayed()));bottomNavigationItemView2.perform(click());

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void navHomeClick(){
        ViewInteraction bottomNavigationItemView = onView(allOf(withId(R.id.bottom_nav_home), withContentDescription("Home"),
                childAtPosition(childAtPosition(withId(R.id.bottomNavigationView), 0), 0), isDisplayed()));
        bottomNavigationItemView.perform(click());

    }

    @Test
    public void navSearchClick(){
        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.bottom_nav_search), withContentDescription("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

    }

    @Test
    public void navAddBookClick(){
        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.bottom_nav_add), withContentDescription("Add Book"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

    }

    @Test
    public void navScanClick(){
        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.bottom_nav_scan), withContentDescription("Scan"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());


    }

    @Test
    public void navProfileClick(){
        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.bottom_nav_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

    }

}

