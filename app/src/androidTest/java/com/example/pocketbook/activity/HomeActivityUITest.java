package com.example.pocketbook.activity;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.View;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.pocketbook.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeActivityUITest {

    @Rule
    public ActivityTestRule<HomeActivity> nActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    private HomeActivity nActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(HomeActivity.class.getName(),null,false);//monitor to show when second activity opens

    @Before //before you execute the test
    public void setUp() throws Exception {
        nActivity = nActivityTestRule.getActivity();

    }

    @Test
    public void TestEditProfileFragmentEditBtnClickable(){
        SystemClock.sleep(800);
        assertNotNull(nActivity.findViewById(R.id.bottom_nav_home));
        SystemClock.sleep(800);
    }


}
