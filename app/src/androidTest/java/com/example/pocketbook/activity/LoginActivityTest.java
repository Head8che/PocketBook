package com.example.pocketbook.activity;

import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {

    private Solo solo;
    private long currentTime = System.currentTimeMillis();
    private String mockLoginEmail = "mocklogin" + currentTime + "@gmail.com";
    private String mockLoginPassword = "123456";

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
     * Runs before each test and creates solo instance. Also navigates to LoginActivity.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

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
        solo.enterText(emailField, mockLoginEmail); //add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, mockLoginPassword);  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        /////////////////////////////////// GO TO LoginActivity ////////////////////////////////////

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        signOut();  // sign out of the created user account

        solo.sleep(2000); // give it time to sign out

        // return to LoginActivity
        solo.goBackToActivity("LoginActivity");

        // Asserts that the current activity is LoginActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**
     * Runs after each test to remove the test user from Firebase.
     */
    @After
    public void removeMockFromFirebase() {
        // login with the mock account so that it can be deleted
        FirebaseAuth.getInstance().signInWithEmailAndPassword("mocklogin" + currentTime
                + "@gmail.com", "123456");
        solo.sleep(5000); // give it time to sign in
        FirebaseIntegrity.deleteCurrentlyLoggedInUser();
    }

    /**
     * Check if the email field exists with assertNotNull.
     * Check if the initial string in the email field is "" with assertEquals.
     * Check if logging in with no email fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     * Check if logging in with an invalid email fails with assertCurrentActivity.
     * Check if the user is specially alerted to the erroneous field with assertTrue.
     * Check if logging in with a non-lowercase email fails with assertCurrentActivity.
     * Check if the user is specially alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidEmailLogin(){
        View loginBtn = solo.getView(R.id.loginLoginBtn);
        TextInputEditText emailField = (TextInputEditText) solo.getView(R.id.loginEmailField);

        assertNotNull(emailField);  // email field exists
        assertEquals("", Objects.requireNonNull(emailField.getText()).toString());

        solo.clickOnView(loginBtn); // click login button

        // Asserts that the current activity is LoginActivity (i.e. login didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));

        solo.enterText(emailField, "mock@.");  // add an invalid email

        solo.clickOnView(loginBtn); // click login button

        // Asserts that the current activity is LoginActivity (i.e. login didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // True if 'Invalid Email' is present
        assertTrue(solo.searchText("Invalid Email"));

        solo.clearEditText(emailField);
        solo.enterText(emailField, "Mock@email.com");  // add an invalid non-lowercase email

        solo.clickOnView(loginBtn); // click login button

        // Asserts that the current activity is LoginActivity (i.e. login didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // True if 'Email must be lowercase!' is present
        assertTrue(solo.searchText("Email must be lowercase!"));
    }

    /**
     * Check if the password field exists with assertNotNull.
     * Check if the initial string in the password field is "" with assertEquals.
     * Check if logging in with no password fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     * Check if logging in with an invalid password fails with assertCurrentActivity.
     * Check if the user is specially alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidPasswordLogin(){
        View loginUpBtn = solo.getView(R.id.loginLoginBtn);
        TextInputEditText emailField = (TextInputEditText) solo.getView(R.id.loginEmailField);
        TextInputEditText passwordField = (TextInputEditText) solo.getView(R.id.loginPasswordField);

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, "mocksignup@gmail.com");  // add an email

        assertNotNull(passwordField);  // password field exists
        assertEquals("", Objects.requireNonNull(passwordField.getText()).toString());

        solo.clickOnView(loginUpBtn); // click login button

        // Asserts that the current activity is LoginActivity (i.e. login didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));

        solo.enterText(passwordField, "12345");  // add an invalid password

        solo.clickOnView(loginUpBtn); // click login button

        // Asserts that the current activity is LoginActivity (i.e. login didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        // True if 'Must be 6 characters or longer!' is present
        assertTrue(solo.searchText("Must be 6 characters or longer!"));
    }

    /**
     * Check if logging in with invalid credentials fails with assertCurrentActivity.
     */
    @Test
    public void checkInvalidCredentialsLogin(){
        View loginUpBtn = solo.getView(R.id.loginLoginBtn);
        TextInputEditText emailField = (TextInputEditText) solo.getView(R.id.loginEmailField);
        TextInputEditText passwordField = (TextInputEditText) solo.getView(R.id.loginPasswordField);

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, "badmocksignup@gmailz.com");  // add an invalid email

        assertNotNull(passwordField);  // password field exists
        solo.enterText(passwordField, String.valueOf(currentTime));  // add a password

        solo.clickOnView(loginUpBtn); // click login button

        // Asserts that the current activity is LoginActivity (i.e. login didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**
     * Check if logging in with valid credentials succeeds with assertCurrentActivity.
     */
    @Test
    public void checkValidCredentialsLogin(){
        View loginUpBtn = solo.getView(R.id.loginLoginBtn);
        TextInputEditText emailField = (TextInputEditText) solo.getView(R.id.loginEmailField);
        TextInputEditText passwordField = (TextInputEditText) solo.getView(R.id.loginPasswordField);

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, mockLoginEmail);  // add a valid email

        assertNotNull(passwordField);  // password field exists
        solo.enterText(passwordField, mockLoginPassword);  // add a valid password

        solo.clickOnView(loginUpBtn); // click login button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        // Asserts that the current activity is HomeActivity (i.e. login redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

}


