package com.example.pocketbook.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.R;
import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
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
import static org.junit.Assert.assertTrue;

public class ViewMyBookRequestsFragmentTest {
    private Solo solo;
    private Book mockBook;
    private Request mockRequest;
    private User mockRequester;

    @Rule
    public ActivityTestRule<HomeActivity> rule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class, true, true) {
                @Override
                protected Intent getActivityIntent() {  // start HomeActivity with User object
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, HomeActivity.class);
                    User mockUser = new User("mockFirstName",
                            "mockLastName","mock@mock.com","mockUsername",
                            "mockPassword", null, null);
                    result.putExtra("CURRENT_USER", mockUser );
                    return result;
                }
            };

    /**
     * runs before each test and creates a solo instance
     * navigates to the ViewMyBookRequestsFragment
     */
    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        mockBook = new Book("mockID", "mockTitle", "mockAuthor",
                "0000000000000", "mock@mock.com", "AVAILABLE", false,
                "this is a test", "GOOD", "", new ArrayList<>());
//        mockBook.pushNewBookToFirebase();
//        mockRequester = new User("mockFirst", "mockLast", "mockuser@gmail.com", "mockUser", "123456", "");
//        mockRequester.setNewUserFirebase();
//        mockRequest = new Request("mockuser@gmail.com", "mock1@mock.com", mockBook);
//        mockBook.addRequest(mockRequest);

        //asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.bottom_nav_profile));  // click on profile button
        solo.clickInRecyclerView(0); //click on the only book in the recycler
        ViewGroup tabs = (ViewGroup) solo.getView(R.id.viewMyBookFragTabLayout);
        View requestsTab = tabs.getChildAt(0);
        solo.clickOnView(requestsTab);

    }

    /**
     * runs after each test
     * removes the mockBook and mockRequester from firebase
     */
    @After
    public void removeMockFromFirebase() {
        FirebaseIntegrity.deleteDocumentsFromSubcollectionOnFieldValue("catalogue",
                "requests",
                "requester", "mockuser@gmail.com");
        FirebaseIntegrity.deleteDocumentsFromCollectionOnFieldValue("users",
                "email", "mockuser@gmail.com");
        FirebaseIntegrity.deleteDocumentsFromCollectionOnFieldValue("catalogue",
                "author", "mockAuthor");
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
     * test whether the info of a request is displayed
     */
    @Test
    public void testDisplayInfo(){
        //asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        //asserts whether the request's info appears on screen
        assertTrue(solo.waitForText(mockRequester.getUsername(), 1, 2000));
        assertTrue(solo.waitForText(mockRequest.getRequestDate(), 1, 2000));
    }

    /**
     * test accept button on a request
     * after clicking on accept, test if the user can click on accept again
     * after clicking on accept, test if the text on the button changes to "Accepted" for the request
     * after clicking on accept, tests whether the user can click on decline on the same request
     */
    @Test
    public void testAcceptRequest(){
        //asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Button acceptRequest = (Button) solo.getView(R.id.itemRequestAcceptButton);
        Button declineRequest = (Button) solo.getView(R.id.itemRequestDeclineButton);
        solo.clickOnView(acceptRequest);//click on accept
        acceptRequest = (Button) solo.getView(R.id.itemRequestAcceptButton); //update the accept button's attributes
        //solo.clickOnView(acceptRequest);
        assertTrue(!acceptRequest.isEnabled());//check if the accept button has been disabled
        assertEquals("Accepted",acceptRequest.getText().toString());
        assertTrue(solo.waitForText("Accepted", 1, 2000));
        assertTrue(!declineRequest.isEnabled());//check if the decline button has been disabled
    }

    /**
     * test the decline button for a request
     * test if the requests disappears after clicking on decline
     */
    @Test
    public void testDeclineRequest(){
        //asserts that the current activity is HomeActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        Button declineRequest = (Button) solo.getView(R.id.itemRequestDeclineButton);
        solo.clickOnView(declineRequest);
        //asserts if the request's info is no longer displayed
        assertFalse(solo.waitForText(mockRequester.getUsername(), 1, 2000));
        assertFalse(solo.waitForText(mockRequest.getRequestDate(), 1, 2000));
    }
}
