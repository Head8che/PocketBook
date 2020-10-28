package com.example.pocketbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pocketbook.R;
import com.example.pocketbook.model.Book;
import com.example.pocketbook.model.BookList;
import com.example.pocketbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketbook.adapter.ViewMyBookPagerAdapter;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ViewMyBookActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private Book book = null;
    private User user = null;
    private BookList catalogue = null;

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

        ViewMyBookPagerAdapter viewMyBookPagerAdapter =
                new ViewMyBookPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), book, catalogue);

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
    }
}