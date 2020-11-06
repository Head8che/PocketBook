package com.example.pocketbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.activity.AddBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.textfield.TextInputEditText;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddBookActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<HomeActivity> rule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class, true, true) {
                @Override
                protected Intent getActivityIntent() {  // start HomeActivity with User object
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, HomeActivity.class);
                    result.putExtra("CURRENT_USER", new User("mockFirstName",
                            "mockLastName","mock@mock.com","mockUsername",
                            "mockPassword", null));
                    return result;
                }
            };

    /**
     * Runs before all tests and creates solo instance. Also navigates to AddBookActivity.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Asserts that the current activity is HomeActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.bottom_nav_add));  // click on add button

        // Asserts that the current activity is AddBookActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);
        solo.sleep(2000); // give it time to change activity
    }

    /**
     * Runs after each test to remove the test Book from Firebase.
     */
    @After
    public void removeMockFromFirebase() {
        FirebaseIntegrity.removeAuthorFromFirestore("M0cK^U+H0R");
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Check if the cancel button redirects to HomeActivity with assertCurrentActivity
     */
    @Test
    public void checkCancel(){
        View cancelBtn = solo.getView(R.id.addBookCancelBtn);

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is HomeActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    /**
     * Check if the Change Photo dialog displays the correct options with assertTrue
     */
    @Test
    public void checkOptions(){
        solo.clickOnText("CHANGE PHOTO"); // Click CHANGE PHOTO text

        // True if the title 'Change Book Photo' is present
        assertTrue(solo.searchText("Change Book Photo"));

        /* True if the options Take Photo and Choose Photo show up on the screen;
        wait at least 2 seconds and find minimum one match for both. */

        assertTrue(solo.waitForText("Take Photo", 1, 2000));
        assertTrue(solo.waitForText("Choose Photo", 1, 2000));
    }

    /**
     * Check if the title field exists with assertNotNull.
     * Check if the initial string in the title field is "" with assertEquals.
     * Check if saving a book with no title fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkNoTitleSave(){
        View saveBtn = solo.getView(R.id.addBookSaveBtn);
        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);

        assertNotNull(titleField);  // title field exists
        assertEquals("", Objects.requireNonNull(titleField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is AddBookActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);

        // True if the title 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if the author field exists with assertNotNull.
     * Check if the initial string in the author field is "" with assertEquals.
     * Check if saving a book with no author fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkNoAuthorSave(){
        View saveBtn = solo.getView(R.id.addBookSaveBtn);
        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);
        TextInputEditText authorField = (TextInputEditText) solo.getView(R.id.addBookAuthorField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        assertNotNull(authorField);  // author field exists
        assertEquals("", Objects.requireNonNull(authorField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is AddBookActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);

        // True if the author 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if the isbn field exists with assertNotNull.
     * Check if the initial string in the isbn field is "" with assertEquals.
     * Check if saving a book with no isbn fails with assertCurrentActivity.
     * Check if the user is alerted to the erroneous field with assertTrue.
     */
    @Test
    public void checkNoIsbnSave(){
        View saveBtn = solo.getView(R.id.addBookSaveBtn);
        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);
        TextInputEditText authorField = (TextInputEditText) solo.getView(R.id.addBookAuthorField);
        TextInputEditText isbnField = (TextInputEditText) solo.getView(R.id.addBookISBNField);

        assertNotNull(titleField); // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        assertNotNull(authorField); // author field exists
        solo.enterText(authorField, "M0cK^U+H0R");  // add an author

        assertNotNull(isbnField); // isbn field exists
        assertEquals("", Objects.requireNonNull(isbnField.getText()).toString());

        solo.clickOnView(saveBtn); // click save button

        // Asserts that the current activity is AddBookActivity (i.e. save didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);

        // True if the isbn 'Input required' is present
        assertTrue(solo.searchText("Input required"));
    }

    /**
     * Check if erroneous field alert is invisible on valid entry with assertFalse.
     * Check if saving a valid book succeeds with assertCurrentActivity.
     */
    @Test
    public void checkValidSave(){
        View saveBtn = solo.getView(R.id.addBookSaveBtn);
        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);
        TextInputEditText authorField = (TextInputEditText) solo.getView(R.id.addBookAuthorField);
        TextInputEditText isbnField = (TextInputEditText) solo.getView(R.id.addBookISBNField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        assertNotNull(authorField);  // author field exists
        solo.enterText(authorField, "M0cK^U+H0R");  // add an author

        assertNotNull(isbnField);  // isbn field exists
        solo.enterText(isbnField, "9781234567890");  // add an isbn

        solo.clickOnView(saveBtn); // click save button

        // False if 'Input required' is present
        assertFalse(solo.searchText("Input required"));

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    /**
     * Check if user is kept in AddBookActivity when they try to exit
     * but have unsaved edits with assertCurrentActivity.
     * Check if discard dialog appears with assertTrue.
     */
    @Test
    public void checkDiscardDialog(){
        View cancelBtn = solo.getView(R.id.addBookCancelBtn);

        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is AddBookActivity (i.e. cancel didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);

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
        View cancelBtn = solo.getView(R.id.addBookCancelBtn);

        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is AddBookActivity (i.e. cancel didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("DISCARD"); // click discard text

        // Asserts that the current activity is HomeActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

    }

    /**
     * Check if user is kept in AddBookActivity
     * when they opt to keep editing with AssertCurrentActivity.
     */
    @Test
    public void checkDiscardDialogKeepEditing(){
        View cancelBtn = solo.getView(R.id.addBookCancelBtn);

        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);

        assertNotNull(titleField);  // title field exists
        solo.enterText(titleField, "Mock Title");  // add a title

        solo.clickOnView(cancelBtn); // click cancel button

        // Asserts that the current activity is AddBookActivity (i.e. cancel didn't redirect).
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);

        // True if the text 'Discard Changes?' is present
        assertTrue(solo.searchText("Discard Changes?"));

        solo.clickOnText("KEEP EDITING"); // click keep editing text

        // Asserts that the current activity is AddBookActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);

    }

}
