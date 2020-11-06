package com.example.pocketbook;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.activity.AddBookActivity;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.User;
import com.example.pocketbook.util.FirebaseIntegrity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ViewBookFragmentTest {
    private Solo solo;
    private Book mockBook;
    private User mockOwner;

    @Rule
    public ActivityTestRule<HomeActivity> rule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class, true, true) {
                @Override
                protected Intent getActivityIntent() {  // start HomeActivity with User object
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, HomeActivity.class);
                    User mockUser = new User("mockFirstName",
                            "mockLastName","mock@mock.com","mockUsername",
                            "mockPassword", null);
                    result.putExtra("CURRENT_USER", mockUser );
                    return result;
                }
            };

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Asserts that the current activity is HomeActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.bottom_nav_home));  // click on home button
        mockOwner = new User("amockFirst", "amockLast", "aaa@test.com", "amockUser", "mock", "");
        mockOwner.setNewUserFirebase();
        mockBook = new Book("0", "mockTitle", "mockAuthor", "0000000000000", "aaa@test.com", "AVAILABLE", "this is a test", "GOOD", "");
        mockBook.pushNewBookToFirebase();
        solo.clickInRecyclerView(0);

    }

    @After
    public void removeMockFromFirebase() {
        FirebaseIntegrity.removeAuthorFromFirestore("mockAuthor");
        FirebaseIntegrity.removeUserFromFirebase("aaa@test.com");
    }

//    @Test
//    public void start(){
//        Activity activity = rule.getActivity();
//        Fragment fragment = solo.getCurrentActivity().getFragmentManager().findFragmentById(R.id.container);
//    }


    @Test
    public void testDisplayInfo(){

        TextView username = (TextView) solo.getView(R.id.viewBookUsernameTextView);
        TextView isbn = (TextView) solo.getView(R.id.viewBookISBN);
        TextView comment = (TextView) solo.getView(R.id.viewBookComment);
        TextView condition = (TextView) solo.getView(R.id.viewBookCondition);

        assertEquals(mockBook.getISBN(),isbn.getText().toString().replace("ISBN: ",""));

        if (mockBook.getComment() != null)
            assertEquals(mockBook.getComment(),comment.getText().toString().replace("Comment: ",""));

        if (mockBook.getCondition() != null)
            assertEquals(mockBook.getCondition(),condition.getText().toString().replace("Condition: ",""));

        assertEquals(mockOwner.getUsername(),username.getText().toString());
    }

    @Test
    public void testRequestBook(){

        Fragment fragment = solo.getCurrentActivity().getFragmentManager().findFragmentById(R.id.container);
        assertEquals(fragment.getTag())
        Button requestBtn = (Button) solo.getView(R.id.viewBookRequestBtn);

        solo.clickOnView(requestBtn);

        assertTrue(mockBook.getRequestList().containsRequest("mock@mock.com"));

        solo.clickInRecyclerView(0);

        assertFalse(requestBtn.isClickable());

    }

}
