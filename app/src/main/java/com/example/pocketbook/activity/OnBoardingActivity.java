package com.example.pocketbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    ViewPager viewPager;
    LinearLayout dotsLayout;
    OnBoardingSliderAdapter sliderAdapter;
    TextView[] dots;
    Button nextBtn;
    Button skipBtn;
    int slideCount = 3;
    int currentPos;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("CURRENT_USER");

        //Hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        nextBtn = findViewById(R.id.next_btn);
        skipBtn = findViewById(R.id.skip_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip();
            }
        });

        //Call adapter
        sliderAdapter = new OnBoardingSliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void skip() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("CURRENT_USER", currentUser);
        startActivity(intent);
        finishAffinity();
    }

    public void next() {
        if (currentPos == (slideCount - 1)) {
            skip();
            return;
        }

        viewPager.setCurrentItem(currentPos + 1);

    }

    private void addDots(int position) {

        dots = new TextView[slideCount];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorUnselected));

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
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
                nextBtn.setText("Finish");
            } else {
                nextBtn.setText("Next");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}