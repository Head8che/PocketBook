package com.example.pocketbook.activity;

import android.view.View;

import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.pocketbook.R;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.textfield.TextInputEditText;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditBookActivityTest {
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
     * Runs before all tests and creates solo instance. Also navigates to EditBookActivity.
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
        solo.enterText(emailField, "mockeditbook" + currentTime + "@gmail.com"); //add email

        assertNotNull(passwordField);
        solo.enterText(passwordField, "123456");  // add a password

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
        solo.enterText(authorField, "M0cK^U+H0R");  // add an author

        assertNotNull(isbnField);  // isbn field exists
        solo.enterText(isbnField, "9781234567897");  // add an isbn

        solo.clickOnView(saveBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        ////////////////////////////////// GO TO EditBookActivity //////////////////////////////////

        View editBtn = solo.getView(R.id.viewMyBookEditBtn);

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(editBtn); // click edit button (we should be in ViewMyBookBookFragment)

        // Asserts that the current activity is EditBookActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);
        solo.sleep(2000); // give it time to change activity
    }

    /**
     * Runs after each test to exit from EditBookActivity
     * and remove the test user from Firebase.
     */
    @After
    public void removeMockFromFirebase() {
        FirebaseIntegrity.deleteCurrentlyLoggedInUser();
        FirebaseIntegrity.deleteDocumentsFromCollectionOnFieldValue("catalogue",
                "author", "M0cK^U+H0R");
    }

    /**
     * Check if the cancel button redirects to HomeActivity with assertCurrentActivity
     */
    @Test
    public void checkCancel(){
        View cancelBtn = solo.getView(R.id.editBookCancelBtn);

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    /**
     * Check if the Change Photo dialog displays the correct options with assertTrue
     */
    @Test
    public void checkOptions(){
        solo.clickOnText("CHANGE PHOTO"); // Click CHANGE PHOTO text

        // True if the title 'Change Photo' is present
        assertTrue(solo.searchText("Change Photo"));

        /* True if the options Take Photo and Choose Photo show up on the screen;
        wait at least 2 seconds and find minimum one match for both. */

        assertTrue(solo.waitForText("Take Photo", 1, 2000));
        assertTrue(solo.waitForText("Choose Photo", 1, 2000));
    }

    /**
     * Check if the title field exists with assertNotNull.
     * Check if the cleared string in the title field is "" with assertEquals.
     * Check if saving a user with no title fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidTitleSave() {
        View saveBtn = solo.getView(R.id.editBookSaveBtn);
        TextInputEditText titleField = (TextInputEditText)
                solo.getView(R.id.editBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.clearEditText(titleField);
        assertEquals("", Objects.requireNonNull(titleField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is EditBookActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if the author field exists with assertNotNull.
     * Check if the cleared string in the author field is "" with assertEquals.
     * Check if saving a user with no author fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidAuthorSave() {
        View saveBtn = solo.getView(R.id.editBookSaveBtn);
        TextInputEditText authorField = (TextInputEditText)
                solo.getView(R.id.editBookAuthorField);

        assertNotNull(authorField);  // author field exists
        solo.clearEditText(authorField);
        assertEquals("", Objects.requireNonNull(authorField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is EditBookActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if the isbn field exists with assertNotNull.
     * Check if the cleared string in the isbn field is "" with assertEquals.
     * Check if saving a user with no isbn fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkInvalidIsbnSave() {
        View saveBtn = solo.getView(R.id.editBookSaveBtn);
        TextInputEditText isbnField = (TextInputEditText)
                solo.getView(R.id.editBookISBNField);

        assertNotNull(isbnField);  // isbn field exists
        solo.clearEditText(isbnField);
        assertEquals("", Objects.requireNonNull(isbnField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is EditBookActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);

        // True if 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if erroneous field alert is invisible on valid entry with assertFalse.
     * Check if saving a valid book succeeds with assertCurrentActivity.
     */
    @Test
    public void checkValidSave(){
        View saveBtn = solo.getView(R.id.editBookSaveBtn);
        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.editBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title2");  // edit the title

        solo.clickOnView(saveBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    /**
     * Check if user is kept in EditBookActivity when they try to exit
     * but have unsaved edits with assertCurrentActivity.
     * Check if discard dialog appears with assertTrue.
     */
    @Test
    public void checkDiscardDialog(){
        View cancelBtn = solo.getView(R.id.editBookCancelBtn);

        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.editBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is EditBookActivity (i.e. cancel didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);

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
     * Check if user is sent to HomeActivity
     * when they opt to discard their changes with AssertCurrentActivity.
     */
    @Test
    public void checkDiscardDialogDiscard(){
        View cancelBtn = solo.getView(R.id.editBookCancelBtn);

        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.editBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is EditBookActivity (i.e. cancel didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("DISCARD"); // click discard text

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

    }

    /**
     * Check if user is kept in EditBookActivity
     * when they opt to keep editing with AssertCurrentActivity.
     */
    @Test
    public void checkDiscardDialogKeepEditing(){
        View cancelBtn = solo.getView(R.id.editBookCancelBtn);

        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.editBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is EditBookActivity (i.e. cancel didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("KEEP EDITING"); // click keep editing text

        // Asserts that the current activity is EditBookActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);

    }

}

