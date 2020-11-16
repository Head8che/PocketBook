package com.example.pocketbook.fragment;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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


public class ViewBookFragmentTest {
    private Solo solo;
    private long currentTime = System.currentTimeMillis();
    private String email1 = "mockviewbook1" + currentTime + "@gmail.com";
    private String email2 = "mockviewbook2" + currentTime + "@gmail.com";
    private String password = "123456";

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
        solo.enterText(emailField, email1); // add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, password);  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

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

        /////////////////////////////////// GO TO LoginActivity ////////////////////////////////////

        View backBtn = solo.getView(R.id.viewMyBookFragBackBtn);

        solo.clickOnView(backBtn); // click back button

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        signOut();  // sign out of the created user account

        solo.sleep(2000); // give it time to sign out

        // return to LoginActivity
        solo.goBackToActivity("LoginActivity");

        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        //////////////////////////////// CREATE A MOCK USER ACCOUNT ////////////////////////////////

        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnView(solo.getView(R.id.loginSignUpBtn));  // click on sign up button

        // Asserts that the current activity is SignUpActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.sleep(2000); // give it time to change activity

        signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);
        lastNameField = (TextInputEditText) solo.getView(R.id.signUpLastNameField);
        usernameField = (TextInputEditText) solo.getView(R.id.signUpUsernameField);
        emailField = (TextInputEditText) solo.getView(R.id.signUpEmailField);
        passwordField = (TextInputEditText) solo.getView(R.id.signUpPasswordField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        solo.enterText(lastNameField, "MockLast");  // add a lastName

        assertNotNull(usernameField);  // username field exists
        solo.enterText(usernameField, "MockUsername2");  // add a username

        assertNotNull(emailField);  // email field exists
        solo.clearEditText(emailField);
        solo.enterText(emailField, email2); // add email

        assertNotNull(passwordField);
        solo.clearEditText(passwordField);
        solo.enterText(passwordField, password);  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        ////////////////////////////////// GO TO ViewBookFragment //////////////////////////////////

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        // gets the recycler for the books
        RecyclerView view = (RecyclerView) solo.getView(R.id.recycler_books);

        // gets the number of books in the recycler
        int numOfBooks = Objects.requireNonNull(view.getAdapter()).getItemCount();

        int position = -1;
        for (int i = 0; i < numOfBooks; i++) {
            // scroll to the book position
            onView(withId(R.id.recycler_books)).perform(scrollToPosition(i));
            RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(i);
            if ((viewHolder != null)  // check if the current book is the mock book
                    && hasDescendant(withText("Mock Title")).matches(viewHolder.itemView)) {
                position = i;
                break;
            }
        }

        // assert that the mock book was actually found
        assertNotEquals(-1, position);

        onView(withId(R.id.recycler_books))  // click on the mock book
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

        solo.sleep(2000); // give it time to change fragments to ViewBookFragment
    }

    /**
     * Runs after each test to remove the mock users and the mock book from Firebase.
     */
    @After
    public void removeMockFromFirebase() {
        if (Objects.equals(Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()).getEmail(), email1)) {
            FirebaseIntegrity.deleteCurrentlyLoggedInUser();
            solo.sleep(5000);  // give it time to complete task
            signOut();  // sign out of the created user account
            solo.sleep(5000);  // give it time to complete task
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email2, password);
            solo.sleep(5000);  // give it time to complete task
            FirebaseIntegrity.deleteCurrentlyLoggedInUser();
            solo.sleep(5000);  // give it time to complete task
            signOut();
        } else if (Objects.equals(
                FirebaseAuth.getInstance().getCurrentUser().getEmail(), email2)) {
            FirebaseIntegrity.deleteCurrentlyLoggedInUser();
            solo.sleep(5000);  // give it time to complete task
            signOut();  // sign out of the created user account
            solo.sleep(5000);  // give it time to complete task
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email1, password);
            solo.sleep(5000);  // give it time to complete task
            FirebaseIntegrity.deleteCurrentlyLoggedInUser();
            solo.sleep(5000);  // give it time to complete task
            signOut();
        }
        FirebaseIntegrity.deleteDocumentsFromSubcollectionOnFieldValue("catalogue",
                "requests", "requester", email2);
        solo.sleep(5000);  // give it time to complete task
        FirebaseIntegrity.deleteDocumentsFromCollectionOnFieldValue("catalogue",
                "author", "M0cKAUtH0R");
    }

    /**
     * Check if the back button returned to HomeActivity with assertCurrentActivity.
     * Check if the bottom nav indicates that we are in HomeFragment with assertEquals.
     */
    @Test
    public void checkBackButton(){

        View backBtn = solo.getView(R.id.viewBookFragBackBtn);

        solo.clickOnView(backBtn); // click back button

        solo.sleep(2000); // give it time to change activity

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        BottomNavigationView bottomNavigation = (BottomNavigationView)
                solo.getView(R.id.bottomNavigationView);

        // assert that the profile bottom navigation item is currently selected
        assertEquals(R.id.bottom_nav_home, bottomNavigation.getSelectedItemId());
    }

    /**
     * Check if the book request worked with assertTrue.
     * Check if requesting the book returned us to HomeFragment with assertEquals.
     */
    @Test
    public void checkRequest(){
        View requestBtn = solo.getView(R.id.viewBookRequestBtn);

        solo.clickOnView(requestBtn); // click request button

        solo.sleep(2000); // give it time to change activity

        assertTrue(solo.waitForText("You have requested Mock Title!"));

        BottomNavigationView bottomNavigation = (BottomNavigationView)
                solo.getView(R.id.bottomNavigationView);

        // assert that the profile bottom navigation item is currently selected
        assertEquals(R.id.bottom_nav_home, bottomNavigation.getSelectedItemId());
    }
    
}
