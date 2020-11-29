package com.example.pocketbook.fragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewAction;
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
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class SearchMainFragmentTest {
    private Solo solo;
    private User mU; // mock User
    private Book mB, mB2; // mock Book

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

        // creating mock user and books
        createMockers();

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
    public void checkSearchQueryForFirstTab() throws Exception {
        // making sure we are in the first tab
        // this works since we only have 2 tabs
        onView(withId(R.id.searchFragPager)).perform(swipeRight());

        // search with author
        onView(withId(R.id.searchView)).perform(typeText(mB.getAuthor()));

        // recycler view that contains item
        RecyclerView v = (RecyclerView) solo.getView(R.id.search_recycler_books);
        RecyclerView.ViewHolder vH = v.findViewHolderForAdapterPosition(0);
        hasDescendant(withText(mB.getAuthor())).matches(vH.itemView);
        solo.sleep(2000);
        onView(isAssignableFrom(EditText.class)).perform(clearText()); // clearing text

        // search with title
        onView(withId(R.id.searchView)).perform(typeText(mB.getTitle()));
        hasDescendant(withText(mB.getTitle())).matches(vH.itemView);
        solo.sleep(2000);
        onView(isAssignableFrom(EditText.class)).perform(clearText());

        // search with title
        onView(withId(R.id.searchView)).perform(typeText(mB.getISBN()));
        hasDescendant(withText(mB.getISBN())).matches(vH.itemView);
        solo.sleep(3000); // give it some time for all animations to stop

        // click on the first book
        onView(withId(R.id.search_recycler_books))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }


    // Checking if search queries work for non-exchange tab
    @Test
    public void checkSearchQueryForSecondTab() throws Exception {
        // making sure we are in the second tab
        // this works since we only have 2 tabs
        onView(withId(R.id.searchFragPager)).perform(swipeLeft());
        solo.sleep(4000); // give it some time for all animations to stop

        // search with author
        onView(withId(R.id.searchView)).perform(typeText(mB.getAuthor()));

        // recycler view that contains item
        RecyclerView v = (RecyclerView) solo.getView(R.id.search_recycler_books);
        RecyclerView.ViewHolder vH = v.findViewHolderForAdapterPosition(0);
        hasDescendant(withText(mB.getAuthor())).matches(vH.itemView);
        // TODO: this assertion fails
        assertEquals(v.getAdapter().getItemCount(), 2);// making sure that only 1 mock book sure
        solo.sleep(2000);
        onView(isAssignableFrom(EditText.class)).perform(clearText()); // clearing text

        // search with title
        onView(withId(R.id.searchView)).perform(typeText(mB.getTitle()));
        hasDescendant(withText(mB.getTitle())).matches(vH.itemView);
        assertEquals(v.getAdapter().getItemCount(), 2);// making sure that only 1 mock book sure
        solo.sleep(2000);
        onView(isAssignableFrom(EditText.class)).perform(clearText());

        // search with title
        onView(withId(R.id.searchView)).perform(typeText(mB.getISBN()));
        hasDescendant(withText(mB.getISBN())).matches(vH.itemView);
        assertEquals(v.getAdapter().getItemCount(), 2);// making sure that only 1 mock book sure
        solo.sleep(3000); // wait for all animations to stop

        // making sure the book is clickable
        onView(withId(R.id.search_recycler_books))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    private void createMockers(){
        // Create Mock User
        mU = new User("mock_fn", "mock_ln", "mock@gmail.com", "mock",
                "123456", "0123456789", "none.jpg");
        FirebaseIntegrity.pushNewUserToFirebaseWithURL(mU, "");

        // Create Mock book
        // this book will be shown in non-exchange tab
        mB = new Book("mock_book_1", "mock_book", "mock_author", "9781234567897", "mock@gmail.com",
                "AVAILABLE", true, "we chilling", "GOOD", "none.jpg" , new ArrayList<String>());
        FirebaseIntegrity.pushNewBookToFirebaseWithURL(mB, "");

        // Create other mock book
        // this book will NOT be shown in non-exchange Tab
        mB2 = new Book("mock_book_2", "mock_book_2", "mock_author", "9781234567897", "mock@gmail.com",
                "REQUESTED", false, "we chilling...again", "GOOD", "none.jpg" , new ArrayList<String>());
        FirebaseIntegrity.pushNewBookToFirebaseWithURL(mB2, "");
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
                solo.sleep(3000);  // give it time to complete task
                signOut();  // sign out of the created user account
            }
            // deleting books
            FirebaseIntegrity.deleteDocumentsFromCollectionOnFieldValue("catalogue",
                    "author", mB.getAuthor());
        }

        // remove book

    }
}
