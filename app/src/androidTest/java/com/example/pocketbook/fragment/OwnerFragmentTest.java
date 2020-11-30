package com.example.pocketbook.fragment;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.AddBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.activity.SignUpActivity;
import com.example.pocketbook.util.FirebaseIntegrity;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OwnerFragmentTest {
    private Solo solo;
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

    /**
     * Runs before all tests and creates solo instance. Also navigates to ViewMyBookFragment.
     */
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
        solo.enterText(emailField, "mockownerfrag" + currentTime + "@gmail.com"); //add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, "123456");  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        ////////////////////////////// SKIP ONBOARDING INSTRUCTIONS ////////////////////////////////
        View skipBtn = solo.getView(R.id.onBoardingActivitySkipBtn);
        solo.clickOnView(skipBtn);

        solo.sleep(2000); // give it time to change activity

        ///////////////////////////////////// ADD A MOCK BOOK //////////////////////////////////////

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.bottom_nav_add));  // click on add button

        // Asserts that the current activity is AddBookActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);
        solo.sleep(2000); // give it time to change activity

        View saveBtn = solo.getView(R.id.addBookSaveBtn);
        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);
        TextInputEditText authorField = (TextInputEditText) solo.getView(R.id.addBookAuthorField);
        TextInputEditText isbnField = (TextInputEditText) solo.getView(R.id.addBookISBNField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        assertNotNull(authorField);  // author field exists
        solo.enterText(authorField, "M0cKAUtH0R");  // add an author

        assertNotNull(isbnField);  // isbn field exists
        solo.enterText(isbnField, "9781234567897");  // add an isbn

        solo.clickOnView(saveBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        /////////////////////////////////// GO TO OwnerFragment ////////////////////////////////////

        View backBtn = solo.getView(R.id.viewMyBookFragBackBtn);

        solo.clickOnView(backBtn); // click back button

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.bottom_nav_profile));  // click on profile button
    }

    /**
     * Runs after each test to exit from ViewMyBookFragment
     * and remove the test user from Firebase.
     */
    @After
    public void removeMockFromFirebase() {
        FirebaseIntegrity.deleteCurrentlyLoggedInUser();
        FirebaseIntegrity.deleteDocumentsFromCollectionOnFieldValue("catalogue",
                "author", "M0cKAUtH0R");
    }

    @Test
    public void testItem() {
        // assert that we are in OwnerFragment and that the book details are visible
        assertTrue(solo.searchText("Mock Title"));  // book title
        assertTrue(solo.searchText("M0cKAUtH0R"));  // book author

        // gets the recycler for the books
        RecyclerView view = (RecyclerView) solo.getView(R.id.profileOwnerRecyclerOwnedBooks);

        // gets the number of books in the recycler
        int numOfBooks = Objects.requireNonNull(view.getAdapter()).getItemCount();

        // assert that the added book is in the recycler
        assertEquals(1, numOfBooks);

        int position = -1;
        for (int i = 0; i < numOfBooks; i++) {
            // scroll to the book position
            onView(withId(R.id.profileOwnerRecyclerOwnedBooks)).perform(scrollToPosition(i));
            RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(i);
            if ((viewHolder != null)  // check if the current book is the mock book
                    && hasDescendant(withText("Mock Title")).matches(viewHolder.itemView)) {
                position = i;
                break;
            }
        }

        // assert that the mock book was actually found
        assertNotEquals(-1, position);

        onView(withId(R.id.profileOwnerRecyclerOwnedBooks))  // click on the mock book
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

        solo.sleep(2000); // give it time to change fragments to ViewMyBookFragment
    }

    @Test
    public void testViewAll() {

        View viewAllBtn = solo.getView(R.id.ViewAllOwnedOwner);

        solo.clickOnView(viewAllBtn); // click view all button

        // assert that we are in OwnedBookFragment and that the book details are visible
        assertTrue(solo.searchText("Mock Title"));  // book title
        assertTrue(solo.searchText("M0cKAUtH0R"));  // book author

        // gets the recycler for the books
        RecyclerView view = (RecyclerView) solo.getView(R.id.ownedBooksRecyclerBooks);

        // gets the number of books in the recycler
        int numOfBooks = Objects.requireNonNull(view.getAdapter()).getItemCount();

        // assert that the added book is in the recycler
        assertEquals(1, numOfBooks);

        onView(withId(R.id.ownedBooksRecyclerBooks))  // click on the mock book
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        solo.sleep(2000); // give it time to change fragments to ViewMyBookFragment

        // assert that we are in ViewMyBookFragment and that the book details are visible
        assertTrue(solo.searchText("Mock Title"));  // book title
        assertTrue(solo.searchText("M0cKAUtH0R"));  // book author
        assertTrue(solo.searchText("9781234567897"));  // book isbn
        assertTrue(solo.searchText("FAIR"));  // book condition
    }
}
