package com.example.pocketbook.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pocketbook.R;
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
    private String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_book);
        TabLayout tabLayout = findViewById(R.id.viewMyBookTabLayout);
        TabItem bookTab = findViewById(R.id.viewMyBookBookTab);
        TabItem requestsTab = findViewById(R.id.viewMyBookRequestsTab);
        ViewPager viewPager = findViewById(R.id.viewMyBookViewPager);

        Bundle extras = getIntent().getExtras();
        bookID = (extras == null) ? null : extras.getString("FIRESTORE_BOOK_UID");

        mAuth = FirebaseAuth.getInstance();
        email = "test@pocketbook.com";
        password = "password5";

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.w("TAG", "signInWithEmail:success");
                            Toast.makeText(ViewMyBookActivity.this, "Authentication success. ",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(ViewMyBookActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });

        String user_email = mAuth.getCurrentUser().getEmail();

        ViewMyBookPagerAdapter viewMyBookPagerAdapter =
                new ViewMyBookPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), user_email, bookID);

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
    }
}