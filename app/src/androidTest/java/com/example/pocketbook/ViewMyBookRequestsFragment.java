package com.example.pocketbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.pocketbook.activity.HomeActivity;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.Request;
import com.example.pocketbook.model.User;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ViewMyBookRequestsFragment {
    private Solo solo;
    private Book mockBook;

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
        solo.clickOnView(solo.getView(R.id.bottom_nav_profile));  // click on home button
        mockBook = new Book("0", "mockTitle", "mockAuthor", "0000000000000", "mock@mock.com", "AVAILABLE", "this is a test", "GOOD", "");
        mockBook.pushNewBookToFirebase();


    }

    @Test
    public void start(){
        Activity activity = rule.getActivity();
    }

    @Test
    public void testDisplayInfo(){
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        mockBook.addRequest(new Request("test@gmail.com", "mock@mock.com", mockBook.getId()));
        solo.clickInRecyclerView(0);
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
        ViewGroup tabs = (ViewGroup) solo.getView(R.id.viewMyBookFragTabLayout);
        View requestsTab = tabs.getChildAt(0);
        solo.clickOnView(requestsTab);
        TextView username = (TextView) solo.getView(R.id.itemRequestUsernameTextView);
        TextView date = (TextView) solo.getView(R.id.itemRequestDateTextView);

    }
}
