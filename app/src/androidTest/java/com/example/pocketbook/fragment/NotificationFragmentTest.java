package com.example.pocketbook.fragment;

import android.util.Log;
import android.view.View;
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
import com.example.pocketbook.util.FirebaseIntegrity;
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

public class NotificationFragmentTest {

    private Solo solo;
    private long currentTime = System.currentTimeMillis();
    private String email1 = "mockviewnoti1" + currentTime + "@gmail.com";
    private String email2 = "mockviewnoti2" + currentTime + "@gmail.com";
    private String username1 = "MockUsername1" + currentTime;
    private String username2 = "MockUsername2" + currentTime;
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
        solo.enterText(usernameField, username1);  // add a unique username

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, email1); // add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, password);  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        ////////////////////////////// SKIP ONBOARDING INSTRUCTIONS ////////////////////////////////

        View skipBtn = solo.getView(R.id.onBoardingActivitySkipBtn);
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

        /////////////////////////////////// GO TO LoginActivity ////////////////////////////////////

        View backBtn = solo.getView(R.id.viewMyBookFragBackBtn);

        solo.clickOnView(backBtn); // click back button

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        Log.e("NOTIFICATION_TEST", "pre-signOut");

        signOut();  // sign out of the created user account

        solo.sleep(2000); // give it time to sign out

        Log.e("NOTIFICATION_TEST", "post-signOut");

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

        ////////////////////////////// SKIP ONBOARDING INSTRUCTIONS ////////////////////////////////

        skipBtn = solo.getView(R.id.onBoardingActivitySkipBtn);
        solo.clickOnView(skipBtn);

        solo.sleep(2000); // give it time to change activity

        ////////////////////////////////// REQUEST BOOK //////////////////////////////////
        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        // gets the recycler for the books
        RecyclerView bookView = (RecyclerView) solo.getView(R.id.homeFragmentRecyclerBooks);

        // gets the number of books in the recycler
        int numOfBooks = Objects.requireNonNull(bookView.getAdapter()).getItemCount();

        int position = -1;
        for (int i = 0; i < numOfBooks; i++) {
            Log.e("NOTIFICATION_TEST", "in-scroll");
            // scroll to the book position
            onView(withId(R.id.homeFragmentRecyclerBooks)).perform(scrollToPosition(i));
            RecyclerView.ViewHolder viewHolder = bookView.findViewHolderForAdapterPosition(i);
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
        solo.sleep(2000);
        View requestBtn = solo.getView(R.id.viewBookRequestBtn);
        solo.clickOnView(requestBtn); //send notification to other user
        solo.sleep(2000); // give it time
        solo.clickOnScreen(100,100);// click on the screen
        solo.sleep(2000);
        ////////////////////////////////// LOGIN AS MOCKUSER1 //////////////////////////////////
        signOut();  // sign out of the created user account

        solo.sleep(2000); // give it time to sign out
        solo.goBackToActivity("LoginActivity");
        solo.sleep(2000);
        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        View loginBtn = solo.getView(R.id.loginLoginBtn);
        TextInputEditText emailFieldLogin = (TextInputEditText)
                solo.getView(R.id.loginEmailField);
        TextInputEditText passwordFieldLogin= (TextInputEditText)
                solo.getView(R.id.loginPasswordField);

        assertNotNull(emailFieldLogin);  // email field exist
        solo.enterText(emailFieldLogin, email1);  // enter email

        assertNotNull(passwordFieldLogin);  // password field exists
        solo.enterText(passwordFieldLogin, password);  // enter password
        solo.clickOnView(loginBtn);
        solo.sleep(2000);
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

    }



    @Test
    public void testRequestBookNotificationIsDisplayed(){

        Button notificationsBtn = (Button) solo.getView(R.id.homeFragmentNotificationBtn);
        solo.clickOnView(notificationsBtn);
        solo.sleep(2000);
        RecyclerView notiView = (RecyclerView) solo.getView(R.id.notificationsViewRecyclerView);

        int numOfNotifications = Objects.requireNonNull(notiView.getAdapter()).getItemCount();
        assertEquals(1,numOfNotifications);

        TextView descriptionNotiView = (TextView) solo.getView(R.id.itemNotiDescriptionTextView);
        TextView usernameNotiView = (TextView)solo.getView(R.id.itemNotiUsernameTextView);
        assertEquals(descriptionNotiView.getText(),"MockUsername2 has requested 'Mock Title'");
        assertEquals(usernameNotiView.getText(),"MockUsername2");

    }

//    @Test
//    public void testRequestDeclinedNotificationIsDisplayed() {
//        View profileIcon = solo.getView(R.id.bottom_nav_profile);
//        solo.clickOnView(profileIcon);
//        solo.sleep(2000);
//        onView(withId(R.id.profileOwnerRecyclerRequestedBooks))  // click on the requested mock book
//                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        solo.sleep(2000);
//        solo.clickOnView(solo.getView(R.id.viewMyBookFragRequestsTab));
//        solo.sleep(2000);
//        solo.clickOnButton("Decline");
//        signOut();
//        solo.sleep(2000); // give it time to sign out
//        solo.goBackToActivity("LoginActivity");
//        solo.sleep(2000);
//        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
//        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
//        View loginBtn = solo.getView(R.id.loginLoginBtn);
//        TextInputEditText emailFieldLogin = (TextInputEditText)
//                solo.getView(R.id.loginEmailField);
//        TextInputEditText passwordFieldLogin= (TextInputEditText)
//                solo.getView(R.id.loginPasswordField);
//
//        assertNotNull(emailFieldLogin);  // email field exist
//        solo.enterText(emailFieldLogin, email2);  // enter email
//
//        assertNotNull(passwordFieldLogin);  // password field exists
//        solo.enterText(passwordFieldLogin, password);  // enter password
//        solo.clickOnView(loginBtn);
//        solo.sleep(2000);
//        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
//
//        Button notificationsBtn = (Button) solo.getView(R.id.homeFragmentNotificationBtn);
//        solo.clickOnView(notificationsBtn);
//        solo.sleep(2000);
//        RecyclerView notiView = (RecyclerView) solo.getView(R.id.notificationsViewRecyclerView);
//
//        int numOfNotifications = Objects.requireNonNull(notiView.getAdapter()).getItemCount();
//        assertEquals(1,numOfNotifications);
//
//        TextView descriptionNotiView = (TextView) solo.getView(R.id.itemNotiDescriptionTextView);
//        TextView usernameNotiView = (TextView)solo.getView(R.id.itemNotiUsernameTextView);
//        assertEquals(descriptionNotiView.getText(),"Your request for 'Mock Title' has been declined");
//        assertEquals(usernameNotiView.getText(),"MockUsername1");
//
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
