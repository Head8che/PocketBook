package com.example.pocketbook.activity;

import android.os.Bundle;

import com.example.pocketbook.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.adapter.ViewMyBookPagerAdapter;

public class ViewMyBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_book);
        TabLayout tabLayout = findViewById(R.id.viewMyBookTabLayout);
        TabItem bookTab = findViewById(R.id.viewMyBookBookTab);
        TabItem requestsTab = findViewById(R.id.viewMyBookRequestsTab);
        ViewPager viewPager = findViewById(R.id.viewMyBookViewPager);
        ViewMyBookPagerAdapter viewMyBookPagerAdapter =
                new ViewMyBookPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(viewMyBookPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}