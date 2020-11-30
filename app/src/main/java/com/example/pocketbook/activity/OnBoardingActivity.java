package com.example.pocketbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pocketbook.R;
import com.example.pocketbook.adapter.OnBoardingSliderAdapter;
import com.example.pocketbook.model.User;

public class OnBoardingActivity extends AppCompatActivity {

    /*
      CODE SOURCE:
      https://www.taimoorsikander.com/onboarding-screen-android-studio-city-guide-app-part-3/
     */

    //Variables
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private OnBoardingSliderAdapter sliderAdapter;
    private TextView[] dots;
    private Button nextBtn;
    private Button skipBtn;
    private int slideCount = 3;
    private int currentPos;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        // make fullscreen
        final WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.statusBars());
        }

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");

        // Hooks
        viewPager = findViewById(R.id.onBoardingActivitySlider);
        dotsLayout = findViewById(R.id.onBoardingActivityDots);
        nextBtn = findViewById(R.id.onBoardingActivityNextBtn);
        skipBtn = findViewById(R.id.onBoardingActivitySkipBtn);

        nextBtn.setOnClickListener(v -> next());

        skipBtn.setOnClickListener(v -> skip());

        // Call adapter
        sliderAdapter = new OnBoardingSliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        // Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    /**
     * Transfers the user from the current activity to the home activity
     */
    public void skip() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("CURRENT_USER", currentUser);
        startActivity(intent);
    }

    /**
     * Goes to the next activity
     */
    public void next() {
        if (currentPos == (slideCount - 1)) {
            skip();
            return;
        }

        viewPager.setCurrentItem(currentPos + 1);

    }

    /**
     * Add dots to the activity
     * @param position gets the current position
     */
    private void addDots(int position) {

        dots = new TextView[slideCount];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(HtmlCompat.fromHtml("•", HtmlCompat.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBorrowed));

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(ContextCompat.getColor(getBaseContext(),
                    R.color.colorBorrowed));
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;
            if (currentPos == (slideCount - 1)) {
                nextBtn.setText(R.string.finish);
            } else {
                nextBtn.setText(R.string.next);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}