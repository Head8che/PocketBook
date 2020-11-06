package com.example.pocketbook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EditProfileActivityTest {
    private Solo solo;
    private User user;

    @Rule
    public ActivityTestRule<HomeActivity> rule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class, true, true) {
                @Override
                protected Intent getActivityIntent() {  // start HomeActivity with User object
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, HomeActivity.class);
                    result.putExtra("CURRENT_USER", user = new User("mockFirstName",
                            "mockLastName", "mock@mock.com", "mockUsername",
                            "mockPassword", null));
                    return result;
                }
            };

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.bottom_nav_profile));
        solo.clickOnView(solo.getView(R.id.edit_profile_button));
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);
        solo.sleep(2000);
    }

    @After
    public void removeUserFromFirestore() {
        FirebaseIntegrity.removeAuthorFromFirestore("M0cK^U+H0R");
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void emptyFirstName(){
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        EditText firstNameField = (EditText) solo.getView(R.id.userProfileFirstName);
        assertNotNull(firstNameField);
        solo.clearEditText(firstNameField);
        solo.clickOnView(saveBtn);
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);
        assertFalse(solo.searchText("Input required"));
    }

    @Test
    public void emptyLastName(){
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        EditText lastNameField = (EditText) solo.getView(R.id.userProfileLastName);
        assertNotNull(lastNameField);
        solo.clearEditText(lastNameField);
        solo.clickOnView(saveBtn);
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);
        assertFalse(solo.searchText("Input required"));
    }

    @Test
    public void emptyuserName(){
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        EditText userNameField = (EditText) solo.getView(R.id.userProfileUserName);
        assertNotNull(userNameField);
        solo.clearEditText(userNameField);
        solo.clickOnView(saveBtn);
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);
        assertFalse(solo.searchText("Input required"));
    }

    @Test
    public void emptyEmail(){
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        EditText emailField = (EditText) solo.getView(R.id.userProfileEmail);
        assertNotNull(emailField);
        solo.clearEditText(emailField);
        solo.clickOnView(saveBtn);
        solo.assertCurrentActivity("Wrong Activity", EditProfileActivity.class);
        assertFalse(solo.searchText("Input required"));
    }


    @Test
    public void cameraOptions() {
        solo.clickOnText("Change Profile Photo");
        assertTrue(solo.searchText("Change Profile Photo"));
        assertTrue(solo.waitForText("Take Photo", 1, 2000));
        assertTrue(solo.waitForText("Choose Photo", 1, 2000));
    }

    @Test
    public void validInputs(){
        View saveBtn = solo.getView(R.id.editProfileSaveBtn);
        EditText firstNameField = (EditText) solo.getView(R.id.userProfileFirstName);
        EditText lastNameField = (EditText) solo.getView(R.id.userProfileLastName);
        EditText userNameField = (EditText) solo.getView(R.id.userProfileUserName);
        EditText emailField = (EditText) solo.getView(R.id.userProfileEmail);
        assertNotNull(emailField);
        assertNotNull(userNameField);
        assertNotNull(lastNameField);
        assertNotNull(firstNameField);
        solo.clearEditText(userNameField);
        solo.clearEditText(lastNameField);
        solo.clearEditText(firstNameField);
        solo.clearEditText(emailField);
        solo.enterText(firstNameField, "mockFirstName");
        solo.enterText(lastNameField, "mockLastName");
        solo.enterText(emailField, "mock@mock.com");
        solo.enterText(userNameField, "mockUsername");
        solo.clickOnView(saveBtn);
    }
}