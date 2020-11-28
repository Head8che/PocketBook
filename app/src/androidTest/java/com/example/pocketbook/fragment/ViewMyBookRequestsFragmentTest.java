package com.example.pocketbook.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.AddBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.activity.LoginActivity;
import com.example.pocketbook.activity.SignUpActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
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

import java.util.ArrayList;
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

public class ViewMyBookRequestsFragmentTest {
    private Solo solo;

    // timeout
    private int timeOut;
    // initialize two accounts with a default password
    private long currentTime = System.currentTimeMillis();
    private String email1 = "mockviewbook1" + currentTime + "@gmail.com";
    private String email2 = "mockviewbook2" + currentTime + "@gmail.com";
    private String password = "123456";


    /**
     * Start from the LoginActivity
     */
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
     * runs before each test and creates a solo instance
     * navigates to the ViewMyBookRequestsFragment
     */
    @Before
    public void setUp(){
        final int timeOut = 2000;
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnView(solo.getView(R.id.loginSignUpBtn));  // click on sign up button

        // Asserts that the current activity is SignUpActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.sleep(2000); // give it time to change activity

        // Create a mock user account, User1
        createMockAccount("mockFirst1", "mockLast2",
                "mockUsername1", email1);
        // Skip the onboarding instructions
        skipOnboarding();

        // Add a new mock book for User1
        addMockBook();

        // Sign out User1 and go to LoginActivity
        returnToLoginActivity();

        // Create another mock user account, User2

        solo.clickOnView(solo.getView(R.id.loginSignUpBtn));  // click on sign up button
        // Asserts that the current activity is SignUpActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.sleep(2000); // give it time to change activity

        createMockAccount("mockFirst2", "mockLast2",
                "mockUsername2", email2);
        // Skip the onboarding instructions
        skipOnboarding();

        // As User2, View the mockBook created by User 1
//        goToViewBookFragment();

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        // gets the recycler for the books
        RecyclerView view = (RecyclerView) solo.getView(R.id.homeFragmentRecyclerBooks);

        // gets the number of books in the recycler
        int numOfBooks = Objects.requireNonNull(view.getAdapter()).getItemCount();

        int position = -1;
        for (int i = 0; i < numOfBooks; i++) {
            Log.e("VIEW_BOOK_TEST", "in-scroll");
            // scroll to the book position
            onView(withId(R.id.homeFragmentRecyclerBooks)).perform(scrollToPosition(i));
            RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(i);
            if ((viewHolder != null)  // check if the current book is the mock book
                    && hasDescendant(withText("Mock Title")).matches(viewHolder.itemView)) {
                position = i;
                break;
            }
        }

        // assert that the mock book was actually found
        assertNotEquals(-1, position);

        onView(withId(R.id.homeFragmentRecyclerBooks))  // click on the mock book
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

        solo.sleep(timeOut); // give it time to change fragments to ViewBookFragment

    }


    @Test
    public void checkMyBookRequestsTab() {
        Log.d("DEBUG", "Entered Test");
        // click the request button
        View requestBtn = solo.getView(R.id.viewBookRequestBtn);
        solo.clickOnView(requestBtn); // click request button
        solo.sleep(timeOut); // give it time to change activity
        assertTrue(solo.waitForText("You have requested Mock Title!"));

        //  press anywhere, in this case Home
        BottomNavigationView bottomNavigation = (BottomNavigationView)
                solo.getView(R.id.bottomNavigationView);
        // assert that the profile bottom navigation item is currently selected
        assertEquals(R.id.bottom_nav_home, bottomNavigation.getSelectedItemId());

        // Log out of User2 and return to the Login Activity
        returnToLoginActivity();
//
//        // Log in as User1
//        View loginBtn = solo.getView(R.id.loginLoginBtn);
//        TextInputEditText emailField = (TextInputEditText)
//                solo.getView(R.id.loginEmailField);
//        TextInputEditText passwordField = (TextInputEditText)
//                solo.getView(R.id.loginPasswordField);
//
//        assertNotNull(emailField);  // email field exists
//        solo.enterText(emailField, email1); // add email
//
//        assertNotNull(passwordField);
//        solo.enterText(passwordField, password);  // add a password
//
//        solo.clickOnView(loginBtn); // click save button
//
//        // False if 'Input required' is present
//        assertFalse(solo.searchText("Input required"));
//
//        solo.sleep(timeOut); // wait for activity to change to Home Activity
//
//        // False if current activity is not Home activity
//        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
//
//        // go to User1's profile
//        // assert that the profile bottom navigation item is currently selected
//        assertEquals(R.id.bottom_nav_profile, bottomNavigation.getSelectedItemId());
//        // assert that the current activity is the profile
//        solo.assertCurrentActivity("Wrong Activity", ProfileExistingFragment.class);

        // check for mockBook


        // go to requests tab
        // check if list is nonempty
        // check for User2's request

    }

    private void signInUser() {
    }

    private void signOutUser() {
         View signOutBtn = solo.getView(R.id.profileExistingSignOut);
         solo.clickOnView(signOutBtn);
         solo.sleep(timeOut);
    }

    private void goToProfile() {

    }

    private void makeRequestForBook() {

    }


    /**
     * Views a Book selected
     */
    private void goToViewBookFragment() {
        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        // gets the recycler for the books
        RecyclerView view = (RecyclerView) solo.getView(R.id.homeFragmentRecyclerBooks);

        // gets the number of books in the recycler
        int numOfBooks = Objects.requireNonNull(view.getAdapter()).getItemCount();

        int position = -1;
        for (int i = 0; i < numOfBooks; i++) {
            Log.e("VIEW_BOOK_TEST", "in-scroll");
            // scroll to the book position
            onView(withId(R.id.homeFragmentRecyclerBooks)).perform(scrollToPosition(i));
            RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(i);
            if ((viewHolder != null)  // check if the current book is the mock book
                    && hasDescendant(withText("Mock Title")).matches(viewHolder.itemView)) {
                position = i;
                break;
            }
        }

        // assert that the mock book was actually found
        assertNotEquals(-1, position);

        onView(withId(R.id.homeFragmentRecyclerBooks))  // click on the mock book
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

        solo.sleep(timeOut); // give it time to change fragments to ViewBookFragment
    }

    /**
     * Creates a mock user account
     */
    public void createMockAccount(String mockFirstName, String mockLastName, String mockUsername, String mockEmail) {
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
        solo.enterText(firstNameField, mockFirstName);  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        solo.enterText(lastNameField, mockLastName);  // add a lastName

        assertNotNull(usernameField);  // username field exists
        solo.enterText(usernameField, mockUsername + currentTime);  // add a unique username

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, mockEmail); // add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, password);  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));
    }

    /**
     * After logging in,
     * skip the onboarding instructions
     */
    private void skipOnboarding() {
        View skipBtn = solo.getView(R.id.onBoardingActivitySkipBtn);
        solo.clickOnView(skipBtn);
        solo.sleep(timeOut); // give it time to change activity

    }

    /**
     * Creates a mock Book and
     * adds it to the catalogue
     */
    private void addMockBook() {
        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.bottom_nav_add));  // click on add button

        // Asserts that the current activity is AddBookActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);
        solo.sleep(timeOut); // give it time to change activity

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

    }

    /**
     * Returns to the LoginActivity
     */
    private void returnToLoginActivity() {

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        Log.e("VIEW_BOOK_TEST", "pre-signOut");

        signOut();  // sign out of the created user account

        solo.sleep(2000); // give it time to sign out

        Log.e("VIEW_MY_BOOK_REQUEST_TEST", "pre-signOut");

        // return to LoginActivity
        solo.goBackToActivity("LoginActivity");

        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

    }


//    @Test
//    public void checkRequestTab() {
//
//    }

//    /**
//     * Gets the Activity
//     * @throws Exception
//     */
//    @Test
//    public void start() throws Exception{
//        Activity activity = rule.getActivity();
//    }

//    /**
//     * test whether the info of a request is displayed
//     */
//    @Test
//    public void testDisplayInfo(){
//        //asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
//        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
//        //asserts whether the request's info appears on screen
//        assertTrue(solo.waitForText(mockRequester.getUsername(), 1, 2000));
//        assertTrue(solo.waitForText(mockRequest.getRequestDate(), 1, 2000));
//    }

//    /**
//     * test accept button on a request
//     * after clicking on accept, test if the user can click on accept again
//     * after clicking on accept, test if the text on the button changes to "Accepted" for the request
//     * after clicking on accept, tests whether the user can click on decline on the same request
//     */
//    @Test
//    public void testAcceptRequest(){
//        //asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
//        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
//        Button acceptRequest = (Button) solo.getView(R.id.itemRequestAcceptButton);
//        Button declineRequest = (Button) solo.getView(R.id.itemRequestDeclineButton);
//        solo.clickOnView(acceptRequest);//click on accept
//        acceptRequest = (Button) solo.getView(R.id.itemRequestAcceptButton); //update the accept button's attributes
//        //solo.clickOnView(acceptRequest);
//        assertTrue(!acceptRequest.isEnabled());//check if the accept button has been disabled
//        assertEquals("Accepted",acceptRequest.getText().toString());
//        assertTrue(solo.waitForText("Accepted", 1, 2000));
//        assertTrue(!declineRequest.isEnabled());//check if the decline button has been disabled
//    }
//
//    /**
//     * test the decline button for a request
//     * test if the requests disappears after clicking on decline
//     */
//    @Test
//    public void testDeclineRequest(){
//        //asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
//        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
//        Button declineRequest = (Button) solo.getView(R.id.itemRequestDeclineButton);
//        solo.clickOnView(declineRequest);
//        //asserts if the request's info is no longer displayed
//        assertFalse(solo.waitForText(mockRequester.getUsername(), 1, 2000));
//        assertFalse(solo.waitForText(mockRequest.getRequestDate(), 1, 2000));
//    }

    /**
     * Runs after each test to remove the mock users and the mock book from Firebase.
     */
    @After
    public void removeMockFromFirebase() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (Objects.equals(Objects.requireNonNull(
                    FirebaseAuth.getInstance().getCurrentUser()).getEmail(), email1)) {
                FirebaseIntegrity.deleteUserNotificationsFromFirebase(email1);
                FirebaseIntegrity.deleteCurrentlyLoggedInUser();
                solo.sleep(5000);  // give it time to complete task
                signOut();  // sign out of the created user account
                solo.sleep(5000);  // give it time to complete task
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email2, password);
                solo.sleep(5000);  // give it time to complete task
                FirebaseIntegrity.deleteUserNotificationsFromFirebase(email2);
                FirebaseIntegrity.deleteCurrentlyLoggedInUser();
                solo.sleep(5000);  // give it time to complete task
                signOut();
            } else if (Objects.equals(
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(), email2)) {
                FirebaseIntegrity.deleteUserNotificationsFromFirebase(email2);
                FirebaseIntegrity.deleteCurrentlyLoggedInUser();
                solo.sleep(5000);  // give it time to complete task
                signOut();  // sign out of the created user account
                solo.sleep(5000);  // give it time to complete task
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email1, password);
                solo.sleep(5000);  // give it time to complete task
                FirebaseIntegrity.deleteUserNotificationsFromFirebase(email1);
                FirebaseIntegrity.deleteCurrentlyLoggedInUser();
                solo.sleep(5000);  // give it time to complete task
                signOut();
            }
        }
        FirebaseIntegrity.deleteDocumentsFromSubcollectionOnFieldValue("catalogue",
                "requests", "requester", email2);
        solo.sleep(5000);  // give it time to complete task
        FirebaseIntegrity.deleteDocumentsFromCollectionOnFieldValue("catalogue",
                "author", "M0cKAUtH0R");
    }

}
