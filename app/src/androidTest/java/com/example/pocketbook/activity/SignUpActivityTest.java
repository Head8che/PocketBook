package com.example.pocketbook.activity;

import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.textfield.TextInputEditText;
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


// TODO: handle Remove Photo stuff in all photo activities, so that they all pass


public class SignUpActivityTest {
    private Solo solo;
    private boolean userWasCreated = false;

    @Rule
    public ActivityTestRule<LogInActivity> rule = new ActivityTestRule<>(LogInActivity.class);

    /**
     * Runs before all tests and signs out any logged in user.
     */
    @BeforeClass
    public static void signOut() {
        FirebaseIntegrity.signOutCurrentlyLoggedInUser();
    }

    /**
     * Runs before each test and creates solo instance. Also navigates to SignUpActivity.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Asserts that the current activity is LogInActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.clickOnView(solo.getView(R.id.RegisterBtn));  // click on add button

        // Asserts that the current activity is SignUpActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.sleep(2000); // give it time to change activity
    }

    /**
     * Runs after each test to remove the test user from Firebase, if the user exists.
     */
    @After
    public void removeMockFromFirebase() {
        if (userWasCreated) {
            FirebaseIntegrity.deleteCurrentlyLoggedInUser();
        }
    }


    /**
     * Check if the cancel button redirects to LogInActivity with assertCurrentActivity
     */
    @Test
    public void checkBackButton(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View backBtn = solo.getView(R.id.signUpBackBtn);

        solo.clickOnView(backBtn); // click back button

        solo.sleep(2000); // give it time to change activity

        // Asserts that the current activity is LogInActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
    }

    /**
     * Check if the Change Photo dialog displays the correct options with assertTrue
     */
    @Test
    public void checkPhotoOptions(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        solo.clickOnText("CHANGE PHOTO"); // Click CHANGE PHOTO text

        // True if the title 'Change Photo' is present
        assertTrue(solo.searchText("Change Photo"));

        /* True if the options Take Photo and Choose Photo show up on the screen;
        wait at least 2 seconds and find minimum one match for both. */

        assertTrue(solo.waitForText("Take Photo", 1, 2000));
        assertTrue(solo.waitForText("Choose Photo", 1, 2000));
    }

    /**
     * Check if the firstName field exists with assertNotNull.
     * Check if the initial string in the firstName field is "" with assertEquals.
     * Check if saving a user with no firstName fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidFirstNameSave(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        assertEquals("", Objects.requireNonNull(firstNameField.getText()).toString());

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if the lastName field exists with assertNotNull.
     * Check if the initial string in the lastName field is "" with assertEquals.
     * Check if saving a user with no lastName fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidLastNameSave(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);
        TextInputEditText lastNameField = (TextInputEditText) solo.getView(R.id.signUpLastNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        assertEquals("", Objects.requireNonNull(lastNameField.getText()).toString());

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if the username field exists with assertNotNull.
     * Check if the initial string in the username field is "" with assertEquals.
     * Check if saving a user with no username fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidUsernameSave(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);
        TextInputEditText lastNameField = (TextInputEditText) solo.getView(R.id.signUpLastNameField);
        TextInputEditText usernameField = (TextInputEditText) solo.getView(R.id.signUpUsernameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        solo.enterText(lastNameField, "MockLast");  // add a lastName

        assertNotNull(usernameField);  // username field exists
        assertEquals("", Objects.requireNonNull(usernameField.getText()).toString());

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if the email field exists with assertNotNull.
     * Check if the initial string in the email field is "" with assertEquals.
     * Check if saving a user with no email fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     * Check if saving a user with an invalid email fails with assertCurrentActivity.
     * Check if the user is specially alerted to the erroneous field with assertTrue.
     * Check if saving a user with a non-lowercase email fails with assertCurrentActivity.
     * Check if the user is specially alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidEmailSave(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);
        TextInputEditText lastNameField = (TextInputEditText) solo.getView(R.id.signUpLastNameField);
        TextInputEditText usernameField = (TextInputEditText) solo.getView(R.id.signUpUsernameField);
        TextInputEditText emailField = (TextInputEditText) solo.getView(R.id.signUpEmailField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        solo.enterText(lastNameField, "MockLast");  // add a lastName

        assertNotNull(usernameField);  // username field exists
        solo.enterText(usernameField, "MockUsername");  // add a username

        assertNotNull(emailField);  // email field exists
        assertEquals("", Objects.requireNonNull(emailField.getText()).toString());

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));

        solo.enterText(emailField, "mock@.");  // add an invalid email

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Invalid Email' is present
        assertTrue(solo.searchText("Invalid Email"));

        solo.clearEditText(emailField);
        solo.enterText(emailField, "Mock@email.com");  // add an invalid non-lowercase email

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Email must be lowercase!' is present
        assertTrue(solo.searchText("Email must be lowercase!"));
    }

    /**
     * Check if the password field exists with assertNotNull.
     * Check if the initial string in the password field is "" with assertEquals.
     * Check if saving a user with no password fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     * Check if saving a user with an invalid password fails with assertCurrentActivity.
     * Check if the user is specially alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidPasswordSave(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);
        TextInputEditText lastNameField = (TextInputEditText) solo.getView(R.id.signUpLastNameField);
        TextInputEditText usernameField = (TextInputEditText) solo.getView(R.id.signUpUsernameField);
        TextInputEditText emailField = (TextInputEditText) solo.getView(R.id.signUpEmailField);
        TextInputEditText passwordField = (TextInputEditText) solo.getView(R.id.signUpPasswordField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        solo.enterText(lastNameField, "MockLast");  // add a lastName

        assertNotNull(usernameField);  // username field exists
        solo.enterText(usernameField, "MockUsername");  // add a username

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, "mock@gmail.com");  // add an email

        assertNotNull(passwordField);  // password field exists
        assertEquals("", Objects.requireNonNull(passwordField.getText()).toString());

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));

        solo.enterText(passwordField, "12345");  // add an invalid password

        solo.clickOnView(signUpBtn); // click save button

        // Asserts that the current activity is SignUpActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if 'Must be 6 characters or longer!' is present
        assertTrue(solo.searchText("Must be 6 characters or longer!"));
    }

    /**
     * Check if erroneous field alert is invisible on valid entry with assertFalse.
     * Check if saving a valid user succeeds with assertCurrentActivity.
     */
    @Test
    public void checkValidSave(){
        userWasCreated = true;  // reset userWasCreated to prevent unnecessary delete attempt

        View signUpBtn = solo.getView(R.id.signUpSignUpBtn);
        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);
        TextInputEditText lastNameField = (TextInputEditText) solo.getView(R.id.signUpLastNameField);
        TextInputEditText usernameField = (TextInputEditText) solo.getView(R.id.signUpUsernameField);
        TextInputEditText emailField = (TextInputEditText) solo.getView(R.id.signUpEmailField);
        TextInputEditText passwordField = (TextInputEditText) solo.getView(R.id.signUpPasswordField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        assertNotNull(lastNameField);  // lastName field exists
        solo.enterText(lastNameField, "MockLast");  // add a lastName

        assertNotNull(usernameField);  // username field exists
        solo.enterText(usernameField, "MockUsername");  // add a username

        assertNotNull(emailField);  // email field exists
        solo.enterText(emailField, "mock@gmail.com");  // add an email

        assertNotNull(passwordField);
        solo.enterText(passwordField, "123456");  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    /**
     * Check if user is kept in SignUpActivity when they try to exit
     * but have unsaved edits with assertCurrentActivity.
     * Check if discard dialog appears with assertTrue.
     */
    @Test
    public void checkDiscardDialog(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View backBtn = solo.getView(R.id.signUpBackBtn);

        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        solo.clickOnView(backBtn); // click back button

        // Asserts that the current activity is SignUpActivity (i.e. back didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        // True if the discard changes body text is present
        assertTrue(solo.searchText("If you go back now, you will lose your changes."));

        // True if the text 'KEEP EDITING' is present
        assertTrue(solo.searchText("KEEP EDITING"));

        // True if the text 'DISCARD' is present
        assertTrue(solo.searchText("DISCARD"));
    }

    /**
     * Check if user is sent to LogInActivity
     * when they opt to discard their changes with AssertCurrentActivity.
     */
    @Test
    public void checkDiscardDialogDiscard(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View backBtn = solo.getView(R.id.signUpBackBtn);

        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        solo.clickOnView(backBtn); // click back button

        // Asserts that the current activity is SignUpActivity (i.e. back didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("DISCARD"); // click discard text

        // Asserts that the current activity is LogInActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);

    }

    /**
     * Check if user is kept in SignUpActivity
     * when they opt to keep editing with AssertCurrentActivity.
     */
    @Test
    public void checkDiscardDialogKeepEditing(){
        userWasCreated = false;  // reset userWasCreated to prevent unnecessary delete attempt

        View backBtn = solo.getView(R.id.signUpBackBtn);

        TextInputEditText firstNameField = (TextInputEditText) solo.getView(R.id.signUpFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        solo.clickOnView(backBtn); // click back button

        // Asserts that the current activity is SignUpActivity (i.e. back didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("KEEP EDITING"); // click keep editing text

        // Asserts that the current activity is SignUpActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);

    }
}
