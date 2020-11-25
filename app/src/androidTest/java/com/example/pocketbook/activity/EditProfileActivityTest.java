package com.example.pocketbook.activity;

import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class EditProfileActivityTest {
    private Solo solo;
    private boolean inEditProfileActivityWithChanges = false;
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
        solo.enterText(emailField, "mockeditprofile" + currentTime + "@gmail.com"); //add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, "123456");  // add a password

        solo.clickOnView(signUpBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        //////////////////////////////// GO TO EditProfileActivity /////////////////////////////////

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.bottom_nav_profile));  // click on profile button
//        solo.clickOnView(solo.getView(R.id.edit_profile_button));  // click on Edit Profile
        solo.clickOnView(solo.getView(R.id.profileExistingEditBtn));

        // Asserts that the current activity is EditProfileActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);
        solo.sleep(2000); // give it time to change activity
    }

    /**
     * Runs after each test to exit from EditProfileActivity
     * and remove the test user from Firebase.
     */
    @After
    public void removeMockFromFirebase() {

        // Exiting the test from EditProfileActivity, without going back to ProfileFragment,
        // causes errors because ProfileFragment gets closed incorrectly by the test.

        if (inEditProfileActivityWithChanges) {

            View cancelBtn = solo.getView(R.id.editProfileCancelBtn);

            solo.clickOnView(cancelBtn); // click cancel button

            // Asserts that the current activity is EditProfileActivity (i.e. no redirect).
            solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

            // True if the text 'Discard Changes?' is present
            assertTrue(solo.searchText("Discard Changes?"));

            solo.clickOnText("DISCARD"); // click discard text

            // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
            solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        }

        FirebaseIntegrity.deleteCurrentlyLoggedInUser();
    }

    /**
     * Check if the cancel button redirects to HomeActivity with assertCurrentActivity
     * Check if the fragment after redirect to ProfileFragment with assertTrue
     * Check if the selected bottom navigation item is correct with assertEquals
     */
    @Test
    public void checkCancelButton() {
        View cancelBtn = solo.getView(R.id.editProfileCancelBtn);

        solo.clickOnView(cancelBtn); // click cancel button

        solo.sleep(2000); // give it time to change activity

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        BottomNavigationView bottomNavigation = (BottomNavigationView)
                solo.getView(R.id.bottomNavigationView);

        // assert that we are in ProfileFragment i.e. that the user's first name and last name
        // are shown, and that Suggested Books is shown, since the user has no books
        assertTrue(solo.searchText("MockFirst"));
        assertTrue(solo.searchText("MockLast"));
        assertTrue(solo.searchText("Suggested Books"));

        // assert that the profile bottom navigation item is currently selected
        assertEquals(R.id.bottom_nav_profile, bottomNavigation.getSelectedItemId());

        inEditProfileActivityWithChanges = false;
    }

    /**
     * Check if the Change Photo dialog displays the correct options with assertTrue
     */
    @Test
    public void checkPhotoOptions(){

        solo.clickOnText("CHANGE PHOTO"); // Click CHANGE PHOTO text

        // True if the title 'Change Photo' is present
        assertTrue(solo.searchText("Change Photo"));

        /* True if the options Take Photo and Choose Photo show up on the screen;
        wait at least 2 seconds and find minimum one match for both. */

        assertTrue(solo.waitForText("Take Photo",
                1, 2000));
        assertTrue(solo.waitForText("Choose Photo",
                1, 2000));

        inEditProfileActivityWithChanges = false;
    }

    /**
     * Check if the firstName field exists with assertNotNull.
     * Check if the cleared string in the firstName field is "" with assertEquals.
     * Check if saving a user with no firstName fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidFirstNameSave() {
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        TextInputEditText firstNameField = (TextInputEditText)
                solo.getView(R.id.editProfileFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.clearEditText(firstNameField);
        assertEquals("", Objects.requireNonNull(firstNameField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is EditProfileActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));

        inEditProfileActivityWithChanges = true;
    }

    /**
     * Check if the lastName field exists with assertNotNull.
     * Check if the cleared string in the lastName field is "" with assertEquals.
     * Check if saving a user with no lastName fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidLastNameSave() {
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        TextInputEditText lastNameField = (TextInputEditText)
                solo.getView(R.id.editProfileLastNameField);

        assertNotNull(lastNameField);  // lastName field exists
        solo.clearEditText(lastNameField);
        assertEquals("", Objects.requireNonNull(lastNameField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is EditProfileActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));

        inEditProfileActivityWithChanges = true;
    }

    /**
     * Check if the username field exists with assertNotNull.
     * Check if the cleared string in the username field is "" with assertEquals.
     * Check if saving a user with no username fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidUsernameSave() {
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        TextInputEditText usernameField = (TextInputEditText)
                solo.getView(R.id.editProfileUsernameField);

        assertNotNull(usernameField);  // username field exists
        solo.clearEditText(usernameField);
        assertEquals("", Objects.requireNonNull(usernameField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is EditProfileActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));

        inEditProfileActivityWithChanges = true;
    }

    /**
     * Check if the phoneNumber field exists with assertNotNull.
     * Check if the cleared string in the phoneNumber field is "" with assertEquals.
     * Check if saving a user with no phoneNumber fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidPhoneNumberSave() {
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        TextInputEditText phoneNumberField = (TextInputEditText)
                solo.getView(R.id.editProfilePhoneNumberField);

        assertNotNull(phoneNumberField);  // phoneNumber field exists
        solo.clearEditText(phoneNumberField);
        assertEquals("", Objects.requireNonNull(phoneNumberField.getText()).toString());

        solo.enterText(phoneNumberField, "1");  // add an invalid phone number

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is EditProfileActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        // True if 'Invalid Phone Number' is present
        assertTrue(solo.searchText("Invalid Phone Number"));

        inEditProfileActivityWithChanges = true;
    }

    /**
     * Check if erroneous field alert is invisible on valid entry with assertFalse.
     * Check if saving a valid profile edit succeeds with assertCurrentActivity.
     * Check if the saved edit shows up in ProfileFragment with assertTrue.
     */
    @Test
    public void checkValidSave() {
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        TextInputEditText usernameField = (TextInputEditText)
                solo.getView(R.id.editProfileUsernameField);

        assertNotNull(usernameField);  // username field exists
        solo.clearEditText(usernameField);
        solo.enterText(usernameField, "newMockUsername");  // add a username
        assertEquals("newMockUsername",
                Objects.requireNonNull(usernameField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        // Asserts that Profile Fragment is updated with the new username
        assertTrue(solo.searchText("newMockUsername"));

        inEditProfileActivityWithChanges = false;
    }

    /**
     * Check if user is kept in EditProfileActivity when they try to exit
     * but have unsaved edits with assertCurrentActivity.
     * Check if discard dialog appears with assertTrue.
     */
    @Test
    public void checkDiscardDialog(){
        View cancelBtn = solo.getView(R.id.editProfileCancelBtn);

        TextInputEditText firstNameField = (TextInputEditText)
                solo.getView(R.id.editProfileFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is EditProfileActivity (i.e. back didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        // True if the discard changes body text is present
        assertTrue(solo.searchText("If you go back now, you will lose your changes."));

        // True if the text 'KEEP EDITING' is present
        assertTrue(solo.searchText("KEEP EDITING"));

        // True if the text 'DISCARD' is present
        assertTrue(solo.searchText("DISCARD"));

        inEditProfileActivityWithChanges = false;
    }

    /**
     * Check if user is sent to LoginActivity
     * when they opt to discard their changes with AssertCurrentActivity.
     */
    @Test
    public void checkDiscardDialogDiscard(){
        View cancelBtn = solo.getView(R.id.editProfileCancelBtn);

        TextInputEditText firstNameField = (TextInputEditText)
                solo.getView(R.id.editProfileFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is EditProfileActivity (i.e. back didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("DISCARD"); // click discard text

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        inEditProfileActivityWithChanges = false;

    }

    /**
     * Check if user is kept in EditProfileActivity
     * when they opt to keep editing with AssertCurrentActivity.
     */
    @Test
    public void checkDiscardDialogKeepEditing(){
        View cancelBtn = solo.getView(R.id.editProfileCancelBtn);

        TextInputEditText firstNameField = (TextInputEditText)
                solo.getView(R.id.editProfileFirstNameField);

        assertNotNull(firstNameField);  // firstName field exists
        solo.enterText(firstNameField, "MockFirst");  // add a firstName

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is EditProfileActivity (i.e. back didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("KEEP EDITING"); // click keep editing text

        // Asserts that the current activity is EditProfileActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);

        inEditProfileActivityWithChanges = false;

    }
}
