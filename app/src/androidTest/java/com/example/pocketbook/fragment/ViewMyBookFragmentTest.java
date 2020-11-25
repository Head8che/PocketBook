package com.example.pocketbook.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.AddBookActivity;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ViewMyBookFragmentTest {
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
        solo.enterText(emailField, "mockviewmybook" + currentTime + "@gmail.com"); //add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, "123456");  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        ////////////////////////////// SKIP ONBOARDING INSTRUCTIONS ////////////////////////////////
        View skipBtn = solo.getView(R.id.skip_btn);
        solo.clickOnView(skipBtn);

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

        ///////////////////////////////// GO TO ViewMyBookFragment /////////////////////////////////

        View backBtn = solo.getView(R.id.viewMyBookFragBackBtn);

        solo.clickOnView(backBtn); // click back button

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.bottom_nav_profile));  // click on profile button

        // assert that we are in OwnerFragment and that the book details are visible
        assertTrue(solo.searchText("Mock Title"));  // book title
        assertTrue(solo.searchText("M0cKAUtH0R"));  // book author

        solo.clickOnView(solo.getView(R.id.itemBookCard));  // click on book

        solo.sleep(2000); // give it time to change fragments to ViewMyBookFragment
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

    /**
     * Check if the starting activity is HomeActivity with assertCurrentActivity.
     * Check if we are in OwnerFragment with assertTrue.
     * Check if the currently selected nav item is the profile nav item with assertTrue.
     */
    @Test
    public void checkBackButton(){

        View backBtn = solo.getView(R.id.viewMyBookFragBackBtn);

        solo.clickOnView(backBtn); // click back button

        solo.sleep(2000); // give it time to change activity

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        BottomNavigationView bottomNavigation = (BottomNavigationView)
                solo.getView(R.id.bottomNavigationView);

        // scroll up to the top of OwnerFragment
        Espresso.onView(ViewMatchers.withId(R.id.userProfileScrollView))
                .perform(ViewActions.swipeDown());

        // assert that we are in OwnerFragment i.e. that the user's first name and last name
        // are shown, and that Edit button is shown
        assertTrue(solo.searchText("MockFirst"));
        assertTrue(solo.searchText("MockLast"));
        assertTrue(solo.searchText("Edit"));

        // assert that the profile bottom navigation item is currently selected
        assertEquals(R.id.bottom_nav_profile, bottomNavigation.getSelectedItemId());
    }

    /**
     * Check if the selected book's details are correct with assertTrue.
     */
    @Test
    public void checkBookDetails() {

        // assert that we are in ViewMyBookFragment and that the book details are visible
        assertTrue(solo.searchText("Mock Title"));  // book title
        assertTrue(solo.searchText("M0cKAUtH0R"));  // book author
        assertTrue(solo.searchText("9781234567897"));  // book isbn
        assertTrue(solo.searchText("FAIR"));  // book condition
    }

    /**
     * Check if the selected book has no requests with with assertFalse.
     */
    @Test
    public void checkEmptyRequest() {

        int fromX, toX, fromY, toY;
        int[] location = new int[2];

        TextView titleField = (TextView) solo.getView(R.id.viewMyBookBookTitleTextView);

        assertNotNull(titleField);  // title field exists
        assertEquals("Mock Title", titleField.getText());  // assert that title is valid

        solo.getText("Mock Title").getLocationInWindow(location);

        fromX = location[0];
        fromY = location[1];

        toX = location[0] - 200;
        toY = fromY;

        solo.drag(fromX, toX, fromY, toY, 10);

        // assert that there are no requests (no ACCEPT and DECLINE button)
        assertFalse(solo.searchText("ACCEPT"));
        assertFalse(solo.searchText("DECLINE"));
    }

    /**
     * Check if deleting a book works and navigates to OwnerFragment with assertTrue.
     * Check if the book is deleted with assertFalse.
     */
    @Test
    public void checkDeleteBook() {

        View deleteBtn = solo.getView(R.id.viewMyBookFragDeleteBtn);

        solo.clickOnView(deleteBtn); // click delete button

        solo.clickOnView(solo.getView(android.R.id.button1));

        BottomNavigationView bottomNavigation = (BottomNavigationView)
                solo.getView(R.id.bottomNavigationView);

        // scroll up to the top of OwnerFragment
        Espresso.onView(ViewMatchers.withId(R.id.userProfileScrollView))
                .perform(ViewActions.swipeDown());

        // assert that we are in OwnerFragment i.e. that the user's first name and last name
        // are shown, and that Edit button is shown
        assertTrue(solo.searchText("MockFirst"));
        assertTrue(solo.searchText("MockLast"));
        assertTrue(solo.searchText("Edit"));

        // assert that the profile bottom navigation item is currently selected
        assertEquals(R.id.bottom_nav_profile, bottomNavigation.getSelectedItemId());

        // assert that we are in OwnerFragment and that the book is not found
        assertFalse(solo.searchText("Mock Title"));  // book title
        assertFalse(solo.searchText("M0cKAUtH0R"));  // book author
    }
}
