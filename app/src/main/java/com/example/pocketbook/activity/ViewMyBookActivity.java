package com.example.pocketbook.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketbook.R;
import com.example.pocketbook.fragment.AddFragment;
import com.example.pocketbook.fragment.HomeFragment;
import com.example.pocketbook.fragment.ProfileFragment;
import com.example.pocketbook.fragment.ScanFragment;
import com.example.pocketbook.fragment.SearchFragment;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.adapter.ViewMyBookPagerAdapter;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ViewMyBookActivity extends AppCompatActivity {

    private Book book = null;
    private User user = null;
    private BookList catalogue = null;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_book);
        TabLayout tabLayout = findViewById(R.id.viewMyBookTabLayout);
        TabItem bookTab = findViewById(R.id.viewMyBookBookTab);
        TabItem requestsTab = findViewById(R.id.viewMyBookRequestsTab);
        ViewPager viewPager = findViewById(R.id.viewMyBookViewPager);

        Intent intent = getIntent();

//        if (extras) {
        book = (Book) intent.getSerializableExtra("BOOK");
//            user = intent.getString("USER");
        catalogue = (BookList) intent.getSerializableExtra("CATALOGUE");
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.viewMyBookToolbar);
        ImageView backButton = (ImageView) findViewById(R.id.viewMyBookBackBtn);
        TextView deleteButton = (TextView) findViewById(R.id.viewMyBookDeleteBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Implement Delete Functionality!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ViewMyBookPagerAdapter viewMyBookPagerAdapter =
                new ViewMyBookPagerAdapter(fm, tabLayout.getTabCount(), book, catalogue);

        viewPager.setAdapter(viewMyBookPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

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

        /* TODO: Handle Pocketbook Footer actions */

        /* TODO: Fix Toolbar on Pocketbook Footer Fragment Navigation */

        BottomNavigationView bottomNav = findViewById(R.id.viewMyBookBottomNav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.bottom_nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.bottom_nav_search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.bottom_nav_add:
                        selectedFragment = new AddFragment();
                        break;
                    case R.id.bottom_nav_scan:
                        selectedFragment = new ScanFragment();
                        break;
                    case R.id.bottom_nav_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                for (Fragment fragment : fm.getFragments()) {
                    fm.beginTransaction().remove(fragment).commit();
                }

                fm.beginTransaction().replace(R.id.coordLayout,selectedFragment).commit();
                return true;
            }
        });
    }

}