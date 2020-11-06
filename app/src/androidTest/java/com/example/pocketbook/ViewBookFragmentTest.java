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

public class ViewBookFragmentTest {
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

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Asserts that the current activity is HomeActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.bottom_nav_home));  // click on home button
        Book mockBook = new Book("0000000", "testTitle", "testAuthor", "074754624X", "jane@gmail.com", "AVAILABLE", "this is a test", "GOOD", "");
        mockBook.pushNewBookToFirebase();

    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
        Fragment fragment = solo.getCurrentActivity().getFragmentManager().findFragmentById(R.id.container);
    }

//    @Test
//    public void testDisplayInfo(){
//        View username = solo.getView(R.id.viewBookUsernameTextView);
//        View isbn = solo.getView(R.id.viewBookISBN);
//        View comment = solo.getView(R.id.viewBookComment);
//        View condition = solo.getView(R.id.viewBookCondition);
//
//        solo.
//
//    }
    @Test
    public void testRequestBook(){
        View requestBtn = solo.getButton(R.id.viewBookRequestBtn);
        solo.clickOnView(requestBtn);

    }

}
