package com.example.pocketbook.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.google.android.material.textfield.TextInputEditText;
import com.robotium.solo.Solo;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
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

        // Asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.bottom_nav_add));  // click on add button

        // Asserts that the current activity is AddBookActivity. Otherwise, show Wrong Activity
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


        View editBtn = solo.getView(R.id.viewMyBookEditBtn);
        solo.clickOnView(editBtn); // click edit button

        TextInputEditText editTitleField = (TextInputEditText) solo.getView(R.id.editBookTitleField);
        TextInputEditText editAuthorField = (TextInputEditText) solo.getView(R.id.editBookAuthorField);
        TextInputEditText editISBNField = (TextInputEditText) solo.getView(R.id.editBookISBNField);


        assertNotNull(editTitleField);  // title field exists
        solo.enterText(editTitleField, " 3rd");  // update a title
        assertNotNull(editAuthorField);  // title author exists
        solo.enterText(editAuthorField, "Jr.");  // update a Author
        assertNotNull(editISBNField);  // title ISBN exists
        solo.enterText(editISBNField, "1010");  // update a ISBN

        View editsaveBtn = solo.getView(R.id.editBookSaveBtn);
        solo.clickOnView(editsaveBtn); // click edit button

        // Asserts that the current activity is HomeActivity (i.e. save redirected).
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

    }


//    @Test
//    public void emptyFirstName(){
//        View saveBtn = solo.getView(R.id.addBookSaveBtn);
//        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);
//        TextInputEditText authorField = (TextInputEditText) solo.getView(R.id.addBookAuthorField);
//        TextInputEditText isbnField = (TextInputEditText) solo.getView(R.id.addBookISBNField);
//
//        assertNotNull(titleField);  // title field exists
//        solo.enterText(titleField, "Mock Title");  // add a title
//        assertNotNull(authorField);  // author field exists
//        solo.enterText(authorField, "M0cK^U+H0R");  // add an author
//        assertNotNull(isbnField);  // isbn field exists
//        solo.enterText(isbnField, "9781234567890");  // add an isbn
//        solo.clickOnView(saveBtn); // click save button
//
//            // False if 'Input required' is present
//        assertFalse(solo.searchText("Input required"));
//
//
//        View editBtn = solo.getView(R.id.viewMyBookEditBtn);
//        solo.clickOnView(editBtn); // click edit button
//
//        TextInputEditText editTitleField = (TextInputEditText) solo.getView(R.id.editBookTitleField);
//        assertNotNull(editTitleField);  // title field exists
//        solo.clearEditText(editTitleField);  // update a title
//
////        solo.assertCurrentActivity("Wrong Activity", EditBookActivity.class);
////        assertTrue(solo.searchText("Input required"));
//
//    }



//
//    @Test
//    public void checkInvalidSave(){
//        View saveBtn = solo.getView(R.id.addBookSaveBtn);
//        TextInputEditText titleField = (TextInputEditText) solo.getView(R.id.addBookTitleField);
//        TextInputEditText authorField = (TextInputEditText) solo.getView(R.id.addBookAuthorField);
//        TextInputEditText isbnField = (TextInputEditText) solo.getView(R.id.addBookISBNField);
//
//        assertNotNull(titleField);  // title field exists
//        solo.enterText(titleField, "Mock Title");  // add a title
//
//        assertNotNull(authorField);  // author field exists
//        solo.enterText(authorField, "M0cK^U+H0R");  // add an author
//
//        assertNotNull(isbnField);  // isbn field exists
//        solo.enterText(isbnField, "9781234567890");  // add an isbn
//
//        solo.clickOnView(saveBtn); // click save button
//
//        // False if 'Input required' is present
//        assertFalse(solo.searchText("Input required"));
//
//
//        View editBtn = solo.getView(R.id.viewMyBookEditBtn);
//        solo.clickOnView(editBtn); // click edit button
//
//        TextInputEditText editTitleField = (TextInputEditText) solo.getView(R.id.editBookTitleField);
//        TextInputEditText editAuthorField = (TextInputEditText) solo.getView(R.id.editBookAuthorField);
//        TextInputEditText editISBNField = (TextInputEditText) solo.getView(R.id.editBookISBNField);
//
//
//
//        assertNotNull(editTitleField);  // title field exists
//        solo.clearEditText(editTitleField);  // update a title
//        assertNotNull(editAuthorField);  // title author exists
//        solo.clearEditText(editAuthorField);  // update a Author
//        assertNotNull(editISBNField);  // title ISBN exists
//        solo.clearEditText(editISBNField);  // update a ISBN
//
////        View editsaveBtn = solo.getView(R.id.editBookSaveBtn);
////        solo.clickOnView(editsaveBtn); // click edit button
////
////        // False if 'Input required' is present
////        assertTrue(solo.searchText("Input required"));
////
////        // Asserts that the current activity is HomeActivity (i.e. save redirected).
////        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
//
//
//    }


}

